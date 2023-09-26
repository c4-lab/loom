package edu.msu.mi.loom

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.mturk.AmazonMTurkClient
import com.amazonaws.services.mturk.AmazonMTurkClientBuilder
import com.amazonaws.services.mturk.model.*

//import com.amazonaws.mturk.service.axis.RequesterService

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

import java.nio.charset.StandardCharsets
import java.util.regex.Matcher
import java.util.stream.Collectors

@Slf4j
@Transactional
class MturkService {

    static String QUAL_NumberHITsApproved = "00000000000000000040"
    static String QUAL_Locale = "00000000000000000071"
    static String QUAL_Adult = "00000000000000000060"
    static String QUAL_PercentApproved = "000000000000000000L0"


    //For parsing qualifications
    static qualifierPattern = ~/(!)?([\w_:-]+)\s*(=|>=|<=|!=|>|<|IN|NOT_IN)?\s*([\w,]+)?/
    static operatorMap = ["="     : Comparator.EqualTo,
                          ">="    : Comparator.GreaterThanOrEqualTo,
                          "<="    : Comparator.LessThanOrEqualTo,
                          "!="    : Comparator.NotEqualTo,
                          ">"     : Comparator.GreaterThan,
                          "<"     : Comparator.LessThan,
                          "IN"    : Comparator.In,
                          "NOT_IN": Comparator.NotIn]


    def adminService

    Map<CrowdServiceCredentials, AmazonMTurkClient> clients = new HashMap<>()
    Properties config


    def getBaseMturkUrl(CrowdServiceCredentials credentials) {
        return ("https://worker${credentials.isSandbox() ? "sandbox" : ""}.mturk.com")
    }

    def getMturkClient(CrowdServiceCredentials credentials) {
        AmazonMTurkClient client = clients.get(credentials)
        if (!client) {
            InputStream stream = this.class.classLoader.getResourceAsStream("global.mturk.properties")
            if (!stream) {
                throw new LoomConfigurationException("Missing global.mturk.properties configuration file")
            }
            config = new Properties()
            config.load(stream)

            String AWS_ACCESS_KEY = credentials.access_key
            String AWS_SECRET_KEY = credentials.secret_key

            String SANDBOX_ENDPOINT = config.getProperty("sandbox_endpoint")
            String PRODUCTION_ENDPOINT = config.getProperty("production_endpoint")
            String SIGNING_REGION = config.getProperty("signing_region")

            BasicAWSCredentials awsCreds = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY)
            AmazonMTurkClientBuilder builder = AmazonMTurkClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
            if (credentials.sandbox) {

                builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(SANDBOX_ENDPOINT, SIGNING_REGION))
            } else {

                builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(PRODUCTION_ENDPOINT, SIGNING_REGION))
            }

            client = (AmazonMTurkClient) builder.build()
            clients.put(credentials, client)
        }
        return client
    }

    /**
     * Force HIT for session to expire
     *
     * @param session
     */
    def forceHITExpiry(MturkTask... tasks) {
        Date now = new Date()
        tasks.each { task ->
            task.hits.each { hit ->
                if (hit.expires > now) {
                    hit.expires = now
                    hit.lastUpdate = now

                    GetHITResult hitresult = getHit(hit.hitId, task.credentials)
                    if (!hitresult.getHIT()) {
                        //shouldn't happen, but maybe we don't really care?
                        hit.lastKnownStatus = "Expired"
                        log.warn("Could not locate HIT?  Possibly deleted by requester.")
                    } else if (hitresult.getHIT().getHITStatus() == "Assignable") {
                        //need to update the hit if it is assignable
                        UpdateExpirationForHITRequest req = new UpdateExpirationForHITRequest()
                        req.setHITId(hit.hitId)
                        log.debug("Now is ${now} and new date is ${new Date()}")
                        req.setExpireAt(new Date(0))
                        getMturkClient(task.credentials).updateExpirationForHIT(req)
                        hit.lastKnownStatus = "Expired"

                    } else {
                        hit.lastKnownStatus = hitresult.getHIT().getHITStatus()
                    }
                    hit.save(flush: true)
                }
            }
        }
    }

    def getHitTemplate(String url) {
        InputStream is = this.class.classLoader.getResourceAsStream("my_question.xml")
        String questionSample = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"))
        questionSample.replace("goToThisLink", url)
    }


    def parseQualifier(String qual) {
        Matcher m = qualifierPattern.matcher(qual)
        Map result = [
                "qual"    : null,
                "operator": null,
                "param"   : null,
        ]
        if (m.matches()) {
            result.qual = m[0][2]
            if (m[0][1] == "!") {
                result.operator = Comparator.DoesNotExist
            } else if (!m[0][3]) {
                result.operator = Comparator.Exists
            } else {
                result.operator = operatorMap.get(m[0][3])
                result.param = (m[0][4] =~ /\d+/).collect {
                    Integer.parseInt(it)
                }
            }
        }
        if (!result.qual || !result.operator) {
            throw new Exception("Qualifier formatting error: ${qual}")
        }

        return result
    }

    def constructQualifier(String definition, CrowdServiceCredentials credentials) {
        Map parse = parseQualifier(definition)
        QualificationType qualType= searchQualificationTypeByString(parse.qual as String, credentials)
        if (!qualType) {
            throw new Exception("Could not identify qualifier ${parse.qual}")
        }
        QualificationRequirement req = new QualificationRequirement()
        req.setQualificationTypeId(qualType.qualificationTypeId)
        req.setComparator(parse.operator as Comparator)
        req.setIntegerValues(parse.param as Collection<Integer>)
        return req
    }

    def constructQualifier(ConstraintTest test, CrowdServiceCredentials credentials) {
        Map parse = parseQualifier(test.buildMturkString())
        CrowdServiceScopedId id = test.constraintProvider.serviceIds.find({
            it.credentials.equals(credentials)
        })
        QualificationType qtype = null
        if (id) {
            qtype = getQualification(id)
        } else {
            String qualId = searchQualificationTypeByString(parse.qual as String, credentials)
            if (!qualId) {
                log.debug("Creating qualification for ${parse.qual} on credentials ${credentials}")
                qtype = createMturkQualification(credentials, test.constraintProvider)
                test.constraintProvider.addToServiceIds(new CrowdServiceScopedId(serviceId: qtype.qualificationTypeId, credentials: credentials))
            }
        }
        QualificationRequirement req = new QualificationRequirement()
        req.setQualificationTypeId(qtype.qualificationTypeId)
        req.setComparator(parse.operator as Comparator)
        req.setIntegerValues(parse.param as Collection<Integer>)
        return req
    }

    def launchMturkTask(List<QualificationRequirement> requirements, MturkTask task) {

        buildMturkQualificationTypes(task)

        if (!requirements) {
            requirements = []
        }
        if (task.mturkAdditionalQualifications) {
            requirements.addAll(task.mturkAdditionalQualifications.split(";").collect {
                return constructQualifier(it, task.credentials)
            })
        }

        def owner = task.owner()
        String url = ""
        if (owner instanceof TrainingSet) {
            url = "${adminService.APPLICATION_BASE_URL}/advancetraining/t/" + owner.id.toString()
        } else {
            url = "${adminService.APPLICATION_BASE_URL}/session/s/" + owner.id.toString()
        }
        String hitTemplate = getHitTemplate(url)
        (1..task.mturkNumberHits).each {
            CreateHITRequest request = new CreateHITRequest()
            request.setMaxAssignments(1)
            request.setLifetimeInSeconds(task.mturkHitLifetimeInSeconds * 60L)
            request.setAssignmentDurationInSeconds(task.mturkAssignmentLifetimeInSeconds * 60L)
            // Reward is a USD dollar amount - USD$0.20 in the example below
            request.setReward(task.basePayment)
            request.setTitle(task.title)
            request.setKeywords(task.keywords)
            request.setDescription(task.description)
            request.setQuestion(hitTemplate)
            request.setQualificationRequirements(requirements)
            def result = getMturkClient(task.credentials).createHIT(request)
            def linkurl = "${getBaseMturkUrl(task.credentials)}/mturk/preview?groupId=${result.getHIT().getHITTypeId()}"
            def expiry = result.getHIT().getExpiration()
            if (!expiry) {
                expiry = new Date(System.currentTimeMillis()+task.mturkHitLifetimeInSeconds * 60 * 1000L)
            }
            MturkHIT loomHit = new MturkHIT(hitId: result.getHIT().getHITId(), hitTypeId: result.getHIT().getHITTypeId(), lastKnownStatus: result.getHIT().getHITStatus(),
                    expires: expiry, url: linkurl, lastUpdate: new Date())
            loomHit.save()
            task.addToHits(loomHit)
        }
        task.save(flush: true)
        return task

    }


    def getDefaultQualifications(int rating = 98, boolean usonly = true, int numHits = 100) {
        List requirements = []
        if (usonly) {
            QualificationRequirement localeRequirement = new QualificationRequirement()
            localeRequirement.setQualificationTypeId(QUAL_Locale)
            localeRequirement.setComparator(Comparator.In)
            List<Locale> localeValues = new ArrayList<>()
            localeValues.add(new Locale().withCountry("US"))
            localeRequirement.setLocaleValues(localeValues)
            requirements.add(localeRequirement)
        }
        if (rating > 0) {
            QualificationRequirement approvalRateRequirement = new QualificationRequirement()
            approvalRateRequirement.setQualificationTypeId(QUAL_PercentApproved)
            approvalRateRequirement.setComparator(Comparator.GreaterThanOrEqualTo)
            List<Integer> approvalRateValues = new ArrayList<>()
            approvalRateValues.add(rating)
            approvalRateRequirement.setIntegerValues(approvalRateValues)
            requirements.add(approvalRateRequirement)
        }
        if (numHits > 0) {
            QualificationRequirement numHitsRequirement = new QualificationRequirement()
            numHitsRequirement.setQualificationTypeId(QUAL_NumberHITsApproved)
            numHitsRequirement.setComparator(Comparator.GreaterThanOrEqualTo)
            List<Integer> numHitsValues = new ArrayList<>()
            numHitsValues.add(numHits)
            numHitsRequirement.setIntegerValues(numHitsValues)
            requirements.add(numHitsRequirement)
        }
        return requirements

    }

    def getConstraintQualifications(Collection<ConstraintTest> tests) {
        List<QualificationRequirement> qualifications = []
        qualifications.addAll(session.sp("constraintTests").collect { ConstraintTest test ->
            return constructQualifier(test, task.credentials)
        })
    }

    def searchQualificationTypeByString(String qualificationType, CrowdServiceCredentials credentials, boolean owned = true) {

        ListQualificationTypesRequest lqtr = new ListQualificationTypesRequest()
        lqtr.setMustBeRequestable(false)
        lqtr.setQuery(qualificationType)
        lqtr.setMustBeOwnedByCaller(owned)
        ListQualificationTypesResult result = getMturkClient(credentials).listQualificationTypes(lqtr)
        List<QualificationType> s = result.getQualificationTypes()
        if (s.size() > 0) {
            log.debug("Found ${qualificationType} -> ${s}")
            return s.get(s.size() - 1)
        }
        return null
    }

    def createMturkQualification(CrowdServiceCredentials credentials, ConstraintProvider provider) throws ServiceException, RequestErrorException {
        AmazonMTurkClient client = getMturkClient(credentials)
        CreateQualificationTypeRequest createQualificationTypeRequest = new CreateQualificationTypeRequest()
        createQualificationTypeRequest.setName(provider.getConstraintTitle())
        createQualificationTypeRequest.setDescription(provider.getConstraintDescription())
        createQualificationTypeRequest.setQualificationTypeStatus(QualificationTypeStatus.Active)
        createQualificationTypeRequest.setKeywords("loom,experiment,game")
        log.debug("Creating qualifiction ${createQualificationTypeRequest}")
        def r = client.createQualificationType(createQualificationTypeRequest)
        log.debug("Qual result is ${r}")
        return r.qualificationType
    }

    def getQualification(CrowdServiceScopedId csid) {
        GetQualificationTypeRequest qtype = new GetQualificationTypeRequest()
        qtype.setQualificationTypeId(csid.serviceId)
        GetQualificationTypeResult response = getMturkClient(csid.credentials).getQualificationType(qtype)
        response.getQualificationType()
    }

    /**
     * Checks to see if there is an existing assignment for this hit, and if not, creates one
     *
     * @param assignmentid
     * @param hitId
     * @return
     */
    def attachAssignment(String assignmentid, String hitId) {

        MturkHIT hit = MturkHIT.findByHitId(hitId)
        if (!hit) {
            log.warn("Could not identify hit")
            return null
        }
        MturkAssignment assignment = hit.assignments.find {
            it.assignmentId == assignmentid
        }

        if (!assignment) {
            assignment = new MturkAssignment(assignmentId: assignmentid)
            hit.addToAssignments(assignment)
            hit.save()
        }
        assignment.lastKnownStatus = "Accepted"
        assignment.lastUpdate = new Date()
        assignment.accepted = new Date()
        assignment.save(flush:true)
        return assignment
    }


    def getHit(String HITId, CrowdServiceCredentials credentials) {

        GetHITRequest hitreq = new GetHITRequest()
        hitreq.setHITId(HITId)
        GetHITResult hitresult = getMturkClient(credentials).getHIT(hitreq)
        return hitresult
    }

    def assignQualification(String workerId, ConstraintProvider provider, Integer value, CrowdServiceCredentials creds) {
        AssociateQualificationWithWorkerRequest aq = new AssociateQualificationWithWorkerRequest()
        def qual = findOrCreateQualificationType(provider, creds, true)
        log.debug("Attempt to assign ${qual} to ${workerId}")
        aq.setQualificationTypeId(qual.getQualificationTypeId())
        aq.setWorkerId(workerId)
        aq.setIntegerValue(value)
        getMturkClient(creds).associateQualificationWithWorker(aq)
    }

    def findOrCreateQualificationType(ConstraintProvider provider, CrowdServiceCredentials credentials, boolean create = true) {
        def qualificationType = searchQualificationTypeByString(provider.constraintTitle, credentials)
        if (!qualificationType && create) {
            qualificationType = createMturkQualification(credentials, provider)
        }
        return qualificationType
    }



    //TODO Everything below this line has yet to be verified with the new CrowdServiceCredentials API


    def getQualificationScore(String qualificationId, String workerId) {

        GetQualificationScoreRequest req = new GetQualificationScoreRequest()
        req.setQualificationTypeId(qualificationId)
        req.setWorkerId(workerId)
        GetQualificationScoreResult result = getMturkClient().getQualificationScore(req)
        result.getQualification()
        return result

    }




    def getQualificationRequirement(ConstraintProvider obj) throws ServiceException, RequestErrorException {
        def qtype = findOrCreateQualificationType(obj)
        QualificationRequirement req = new QualificationRequirement()
        req.setQualificationTypeId(qtype)
        return req
    }


    def hasQualification(String workerId, String qualification) {

        ListWorkersWithQualificationTypeRequest request = new ListWorkersWithQualificationTypeRequest()
        request.setQualificationTypeId(searchQualificationTypeByString(qualification))
        ListWorkersWithQualificationTypeResult result = getMturkClient().listWorkersWithQualificationType(request)
        List<Qualification> quals = result.getQualifications()
        boolean flag = false
        for (Qualification qual : quals) {
            if (qual.getWorkerId() == workerId) {
                flag = true
            }
        }
        return flag

    }



    def checkQualificationid(String qualid) {
        log.debug("Checking the existence of qualifier: ${qualid}")
        GetQualificationTypeRequest qtype = new GetQualificationTypeRequest()
        qtype.setQualificationTypeId(qualid)
        GetQualificationTypeResult response = getMturkClient().getQualificationType(qtype)
        return response?.qualificationType != null
    }

    def sendExperimentBonus(String assignmentId, float max_score, float mean_score, def wait_time, def session_id, def worker_id) {

        SendBonusRequest req = new SendBonusRequest()
        Experiment exp = Session.get(session_id).exp
        def score_payment = exp.score
        def finished_payment = exp.completion
        Float payment
//        Float payment = 0.1
        if (mean_score) {
            payment = 0.5f * (max_score + mean_score) * score_payment + finished_payment + wait_time * exp.waiting
        } else {
            payment = finished_payment + wait_time * exp.waiting
        }

        req.setAssignmentId(assignmentId)
        req.setWorkerId(worker_id)
        req.setBonusAmount(String.format("%. 2f", payment))
        req.setReason("Bonus for Story Loom Experiment")
        SendBonusResult result = getMturkClient().sendBonus(req)
    }

//    def sendExperimentWaitingBonus(String assignmentId, def wait_time, def session_id){
//        SendBonusRequest req = new SendBonusRequest();
//        def waiting = Session.get(session_id).exp.waiting
//        float payment = wait_time * waiting
//        req.setAssignmentId(assignmentId);
//        req.setWorkerId(getWorkerId(assignmentId));
//        req.setBonusAmount(payment.toString());
//        req.setReason("finish experiment");
//        SendBonusResult result = getMturkClient().sendBonus(req);
//    }

//    def blockWorker(String workerId){
//        CreateWorkerBlockRequest req = new CreateWorkerBlockRequest()
//        req.setWorkerId(workerId)
//        CreateWorkerBlockResult result = getMturkClient().createWorkerBlock(req)
//    }

    def getWorkerId(String assignmentId) {

        GetAssignmentRequest req = new GetAssignmentRequest()
        req.setAssignmentId(assignmentId)
        GetAssignmentResult result = getMturkClient().getAssignment(req)
        String workerId = result.getAssignment().getWorkerId()
        return workerId
    }

    def listAssighmentsForHIT(String HITId) {

        ListAssignmentsForHITRequest req = new ListAssignmentsForHITRequest()
        req.setHITId(HITId)
        ListAssignmentsForHITResult result = getMturkClient().listAssignmentsForHIT(req)
        if (result.getAssignments()) {
            return result.getAssignments().get(0).getAssignmentId()
        }
        return null
    }



    def deleteHit(MturkHIT hit) {


        DeleteHITRequest dhr = new DeleteHITRequest()

        dhr.setHITId(hit.hitId)
        getMturkClient(hit.credentials).deleteHIT(dhr)

    }


    /**
     * Not using this for now.  All payments will be handled outside of the system
     * @param session
     * @return
     */
    def check_session_payable(Session session) {

        int count = UserSession.countBySession(session)
        def HITIds = session.getHITId()
        // #submitted+#approved
        int total = 0
        int payable = 0
        List payableHIT = new ArrayList()
        for (String HITId : HITIds) {
            try {
                String assignmentId = listAssighmentsForHIT(HITId)
                if (assignmentId) {
                    GetAssignmentRequest req = new GetAssignmentRequest()
                    req.setAssignmentId(assignmentId)
                    GetAssignmentResult result = getMturkClient().getAssignment(req)
                    String status = result.getAssignment().getAssignmentStatus()
                    if (status == "Submitted" || (status == "Approved" && listBonus(HITId).size() == 0)) {
                        payable += 1
                        payableHIT.add(HITId)

                    }
                    if (status == "Submitted" || status == "Approved") {
                        total += 1

                    }
                }
            } catch (Exception e) {
                log.warn("Could not process hit id ${HITId}", e)
            }

        }
        session.paid = total - payable
        session.total = total
        session.save(flush: true)

        return [payableHIT, total - payable, total, count]
    }

    def check_trainingset_payable(TrainingSet trainingSet) {

        def HITIds = trainingSet.getHITId()
        int total = 0
        int payable = 0
        List payableHIT = new ArrayList()
        for (String HITId : HITIds) {
            try {
                String assignmentId = listAssighmentsForHIT(HITId)
                if (assignmentId) {
                    GetAssignmentRequest req = new GetAssignmentRequest()
                    req.setAssignmentId(assignmentId)
                    GetAssignmentResult result = getMturkClient().getAssignment(req)
                    String status = result.getAssignment().getAssignmentStatus()
                    if (status == "Submitted") {
                        payable += 1
                        payableHIT.add(HITId)

                    }
                    if (status == "Submitted" || status == "Approved") {
                        total += 1

                    }
                }
            } catch (Exception e) {
                log.warn("Could not process hit id ${HITId}", e)
            }

        }
        trainingSet.paid = total - payable
        trainingSet.total = total
        trainingSet.save(flush: true)

        return [payableHIT, total - payable, total]
    }

    def listBonus(String HITId) {

        ListBonusPaymentsRequest req = new ListBonusPaymentsRequest()
        req.setHITId(HITId)
        ListBonusPaymentsResult result = getMturkClient().listBonusPayments(req)
        return result.getBonusPayments()
    }

    def pay_session_HIT(Session session) {

        def (payableHIT, paid, total, count) = check_session_payable(session)
        for (String HITId : payableHIT) {
            String assignmentId = listAssighmentsForHIT(HITId)
            GetAssignmentRequest req = new GetAssignmentRequest()
            req.setAssignmentId(assignmentId)
            GetAssignmentResult result = getMturkClient().getAssignment(req)
            String state = result.getAssignment().getAssignmentStatus()
            if (state == "Submitted") {
                ApproveAssignmentRequest areq = new ApproveAssignmentRequest()
                areq.setAssignmentId(assignmentId)
                getMturkClient().approveAssignment(areq)
            }

            String worker_id = getWorkerId(assignmentId)
            User user = User.findByTurkerId(worker_id)
            UserSession us = UserSession.findBySessionAndUser(session, user)
            if (us) {
                int wait_time = us?.wait_time ?: 0
                List scores = UserRoundStory.findAllBySessionAndUserAlias(session, us.userAlias).sort { it.round }.score
                float total_score = scores.sum() / (float) scores.size()
                float max_score = scores.max() as float
                sendExperimentBonus(assignmentId, max_score ? max_score : 0, total_score ? total_score : 0, wait_time, session.id, worker_id)
            } else {
                sendExperimentBonus(assignmentId, 0, 0, 0, session.id, worker_id)
            }


        }
    }

    def pay_trainingset_HIT(TrainingSet trainingSet) {

        def (payableHIT, paid, total) = check_trainingset_payable(trainingSet)
        for (String HITId : payableHIT) {
            String assignmentId = listAssighmentsForHIT(HITId)
            GetAssignmentRequest req = new GetAssignmentRequest()
            req.setAssignmentId(assignmentId)
            GetAssignmentResult result = getMturkClient().getAssignment(req)
            String state = result.getAssignment().getAssignmentStatus()
            if (state == "Submitted") {
                ApproveAssignmentRequest areq = new ApproveAssignmentRequest()
                areq.setAssignmentId(assignmentId)
                getMturkClient().approveAssignment(areq)
            }
        }
    }

    def rejectAssignment(String assignmentId) {

        RejectAssignmentRequest request = new RejectAssignmentRequest()
        request.setAssignmentId(assignmentId)
        getMturkClient().rejectAssignment(request)
    }


    void buildMturkQualificationTypes(MturkTask mturkTask) {
        Collection<ConstraintProvider> providers = new ArrayList()
        if (mturkTask.owner() instanceof TrainingSet) {
            TrainingSet trainingSet = mturkTask.owner()
            providers.addAll(trainingSet.allSubConstraints())
        } else {
            Session s = mturkTask.owner()
            providers.addAll(s.sp("constraintTests").collect { ConstraintTest it ->
                it.constraintProvider
            })
        }
        providers.each { ConstraintProvider provider ->
            CrowdServiceScopedId id = provider.serviceIds.find({
                it.credentials.equals(mturkTask.credentials)
            })
            if (!id) {
                String qualId = searchQualificationTypeByString(provider.getConstraintTitle(), mturkTask.credentials)
                if (!qualId) {
                    log.debug("Creating qualification for ${provider.getConstraintTitle()} on credentials ${mturkTask.credentials}")
                    QualificationType qtype = createMturkQualification(mturkTask.credentials, provider)
                    provider.addToServiceIds(new CrowdServiceScopedId(serviceId: qtype.qualificationTypeId, credentials: mturkTask.credentials))
                }
            }
        }

    }
}
