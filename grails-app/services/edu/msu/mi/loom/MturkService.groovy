package edu.msu.mi.loom

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder

//import com.amazonaws.mturk.service.axis.RequesterService
import com.amazonaws.services.mturk.AmazonMTurk
import com.amazonaws.services.mturk.AmazonMTurkClient
import com.amazonaws.services.mturk.AmazonMTurkClientBuilder
import com.amazonaws.services.mturk.model.*
import grails.transaction.Transactional
import groovy.util.logging.Slf4j

import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

@Slf4j
@Transactional
class MturkService {

    AmazonMTurkClient client
    Properties config

    def isSandbox() {
        if (!config) {
            getMturkClient()
        }
        return Boolean.parseBoolean(config.getProperty("sandbox"))
    }

    def getBaseMturkUrl() {
        return ("https://worker${isSandbox() ? "sandbox" : ""}.mturk.com")
    }

    def getMturkClient() {
        if (!client) {
            //        FilePropertiesConfig config
            InputStream stream = this.class.classLoader.getResourceAsStream("global.mturk.properties")
            if (!stream) {
                throw new LoomConfigurationException("Missing global.mturk.properties configuration file")
            }
            config = new Properties()
            config.load(stream);
            String AWS_ACCESS_KEY = config.getProperty("access_key")
            String AWS_SECRET_KEY = config.getProperty("secret_key")
            String SANDBOX_ENDPOINT = config.getProperty("sandbox_endpoint")
            String SIGNING_REGION = config.getProperty("signing_region")
            boolean sandbox = Boolean.parseBoolean(config.getProperty("sandbox"))
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
            AmazonMTurkClientBuilder builder = AmazonMTurkClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds));
            if (sandbox) {
                println("**************** SANDBOX ENDPOINT SELECTED ****************")
                builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(SANDBOX_ENDPOINT, SIGNING_REGION));
            } else {
                println("**************** PRODUCTION ENDPOINT SELECTED ****************")
                String PRODUCTION_ENDPOINT = config.getProperty("production_endpoint")
                builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(PRODUCTION_ENDPOINT, SIGNING_REGION))
            }

            client = (AmazonMTurkClient)builder.build()

        }
        return client
    }

    def createQualification(HasQualification obj) {
        createQualification(obj.qualificationString,obj.qualificationDescription)
    }




    def createQualification(String qualificationName, String description) throws ServiceException, RequestErrorException{


        def qualId = null

        def s = searchQualificationTypeByString(qualificationName)
        if (s && s.size() > 0) {
            log.warn("Qualification ${qualificationName} already exists: ${s}")

        } else {
            CreateQualificationTypeRequest createQualificationTypeRequest = new CreateQualificationTypeRequest();
            createQualificationTypeRequest.setName(qualificationName);
            //createQualificationTypeRequest.setQualificationTypeStatus("Active");
            createQualificationTypeRequest.setDescription(description);
            createQualificationTypeRequest.setQualificationTypeStatus(QualificationTypeStatus.Active)
            createQualificationTypeRequest.setKeywords("loom,training,game");
            print("Creating qualification with name ${qualificationName} because it does not apparently exist")
            log.debug("Creating qualifiction ${createQualificationTypeRequest}")
            def r = getMturkClient().createQualificationType(createQualificationTypeRequest);
            log.debug("Qual result is ${r}")
            qualId = r.qualificationType.qualificationTypeId
        }

        return qualId

    }

    def assignQualification(String workerId, HasQualification q, def Double value) {
        Integer ivalue = Math.floor(value).toInteger()
        AssociateQualificationWithWorkerRequest aq = new AssociateQualificationWithWorkerRequest();
        def qual = searchQualificationType(q)
        log.debug("Attempt to assign ${qual} to ${workerId}")
        aq.setQualificationTypeId(qual);
        aq.setWorkerId(workerId);
        aq.setIntegerValue(ivalue)
        getMturkClient().associateQualificationWithWorker(aq);
    }



    def getQualificationScore(String qualificationId, String workerId) {

        GetQualificationScoreRequest req = new GetQualificationScoreRequest()
        req.setQualificationTypeId(qualificationId)
        req.setWorkerId(workerId)
        GetQualificationScoreResult result = getMturkClient().getQualificationScore(req)
        result.getQualification()
        return result

    }

    def searchQualificationType(HasQualification obj, boolean create=true) {
        def qid = searchQualificationTypeByString(obj.qualificationString)
        if (!qid && create) {
            qid = createQualification(obj.qualificationString,obj.qualificationDescription)
        }
        return qid

    }

    def searchQualificationTypeByString(String qualificationType) {

        ListQualificationTypesRequest lqtr = new ListQualificationTypesRequest();
        lqtr.setMustBeRequestable(false);
        lqtr.setMustBeOwnedByCaller(true)
        lqtr.setQuery(qualificationType);
        ListQualificationTypesResult result = getMturkClient().listQualificationTypes(lqtr);
        List<QualificationType> s = result.getQualificationTypes();
        if (s.size() > 0) {
            log.debug("Found ${qualificationType} -> ${s}")
            return s.get(s.size() - 1).getQualificationTypeId()
        }
        return null
    }


    def createExperimentHIT(Experiment exp, String sessionId, int num_hits, int assignmentLifetime, int hitLifetime, String fullUrl) throws IOException {
        if (num_hits > 0) {
            Session session = Session.get(sessionId)

            // QualificationRequirement: Locale IN (US, CA)
            String qualifier = exp.qualifier
            Collection<QualificationRequirement> qualificationRequirements = new ArrayList<>()
            //String questionSample = new String(Files.readAllBytes(Paths.get('grails-app/conf/my_question.xml')));
            InputStream is = this.class.classLoader.getResourceAsStream("my_question.xml")
            String questionSample = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            questionSample = questionSample.replace("goToThisLink", "${fullUrl}/session/s/" + session.id.toString())
            if (qualifier) {
                List qualifiers = qualifier.split(";")
                QualificationRequirement performanceRequirement = getQualificationRequirement(exp.training_set.simulations.first())
                performanceRequirement.setComparator(Comparator.GreaterThanOrEqualTo);
                List<Integer> performanceValues = new ArrayList<>();
                performanceValues.add(qualifiers.get(4).split(">=")[1] as Integer);
                performanceRequirement.setIntegerValues(performanceValues)
                qualificationRequirements.add(performanceRequirement)

                QualificationRequirement readingRequirement = getQualificationRequirement(Reading.first());
                readingRequirement.setComparator(Comparator.GreaterThanOrEqualTo);
                List<Integer> readingValues = new ArrayList<>();
                readingValues.add(qualifiers.get(5).split(">=")[1] as Integer);
                readingRequirement.setIntegerValues(readingValues)
                qualificationRequirements.add(readingRequirement)

                QualificationRequirement vaccineRequirement = getQualificationRequirement(Survey.first())
                vaccineRequirement.setComparator(Comparator.In);
                List<Integer> vaccineValues = new ArrayList<>();
                (qualifiers.get(6).split("<=")[0]..qualifiers.get(6).split("<=")[2]).each { n ->
                    vaccineValues.add(n as Integer);
                }
                vaccineRequirement.setIntegerValues(vaccineValues)
                qualificationRequirements.add(vaccineRequirement)

                QualificationRequirement numHitsRequirement = new QualificationRequirement();
                numHitsRequirement.setQualificationTypeId("3CNIZ8EIUVQZYD8YHMEU9ANVZY73BK");
                numHitsRequirement.setComparator(Comparator.GreaterThanOrEqualTo);
                List<Integer> numHitsValues = new ArrayList<>();
                numHitsValues.add(500);
                numHitsRequirement.setIntegerValues(numHitsValues)
                qualificationRequirements.add(numHitsRequirement)

            }

            //Locale requirement
            QualificationRequirement localeRequirement = new QualificationRequirement();
            localeRequirement.setQualificationTypeId("00000000000000000071");
            localeRequirement.setComparator(Comparator.In);
            List<Locale> localeValues = new ArrayList<>();
            localeValues.add(new Locale().withCountry("US"));
            localeRequirement.setLocaleValues(localeValues);
            qualificationRequirements.add(localeRequirement)

            //No repeat stories requirement
            QualificationRequirement storyRequirement = getQualificationRequirement(exp.story)
            storyRequirement.setComparator(Comparator.DoesNotExist);
            qualificationRequirements.add(storyRequirement)

            //Training set requirement
            QualificationRequirement trainingSetRequirement = getQualificationRequirement(exp.training_set)
            trainingSetRequirement.setComparator(Comparator.Exists)
            qualificationRequirements.add(trainingSetRequirement)

//            QualificationRequirement approvalRateRequirement = new QualificationRequirement();
//            approvalRateRequirement.setQualificationTypeId("000000000000000000L0");
//            approvalRateRequirement.setComparator(Comparator.GreaterThanOrEqualTo);
//            List<Integer> approvalRateValues = new ArrayList<>();
//            approvalRateValues.add(98);
//            approvalRateRequirement.setIntegerValues(approvalRateValues)
//            qualificationRequirements.add(approvalRateRequirement)


            //int max_HIT_num = exp.max_node
            List<String> hits = new ArrayList<>()
            List<String> hitTypes = new ArrayList<>()
            (1..num_hits).each {
                CreateHITRequest request = new CreateHITRequest();
                request.setMaxAssignments(1);
                request.setLifetimeInSeconds(hitLifetime * 60L);
                request.setAssignmentDurationInSeconds(assignmentLifetime * 60L);
                // Reward is a USD dollar amount - USD$0.20 in the example below
                request.setReward(exp.accepting as String);
                request.setTitle("Story Loom Session: ${session.exp.name} [${session.id}]");
                request.setKeywords("game, research");
                request.setDescription("Play an online, multiplayer puzzle game for a research study");
                request.setQuestion(questionSample);
                request.setQualificationRequirements(qualificationRequirements);


                def result = getMturkClient().createHIT(request);
                def linkurl = "${getBaseMturkUrl()}/mturk/preview?groupId=${result.getHIT().getHITTypeId()}"
                println(linkurl)
                log.debug(linkurl)
                hits.add(result.getHIT().getHITId())
                hitTypes.add(result.getHIT().getHITTypeId())

            }
            session.HITId = hits
            session.HITTypeId = hitTypes
            session.save()
        }

    }

    def getQualificationRequirement(HasQualification obj) throws ServiceException, RequestErrorException {
        def qtype = searchQualificationType(obj)
        QualificationRequirement req = new QualificationRequirement();
        req.setQualificationTypeId(qtype);
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

    def createTrainingHIT(TrainingSet trainingSet, int num_hits, int assignmentLifetime, int hitLifetime, String fullUrl) throws IOException {

        //log.debug("Got context path ${contextPath}")

        if (num_hits > 0) {

            // QualificationRequirement: Locale IN (US, CA)
            String qualifier = trainingSet.qualifier
            //String questionSample = new String(Files.readAllBytes(Paths.get('grails-app/conf/my_question.xml')))
            InputStream is = this.class.classLoader.getResourceAsStream("my_question.xml")
            String questionSample = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            questionSample = questionSample.replace("goToThisLink", "${fullUrl}/training/t/" + trainingSet.id.toString())
            QualificationRequirement localeRequirement = new QualificationRequirement();
            localeRequirement.setQualificationTypeId("00000000000000000071");
            localeRequirement.setComparator(Comparator.In);
            List<Locale> localeValues = new ArrayList<>();
            localeValues.add(new Locale().withCountry("US"));
            localeRequirement.setLocaleValues(localeValues);
            Collection<QualificationRequirement> qualificationRequirements = new ArrayList<>()
            qualificationRequirements.add(localeRequirement)

            if (qualifier) {
                QualificationRequirement trainingRequirement = new QualificationRequirement();
                trainingRequirement.setQualificationTypeId(searchQualificationTypeByString(TrainingSet.constructQualificationString(trainingSet)))
                trainingRequirement.setComparator(Comparator.DoesNotExist)
                qualificationRequirements.add(trainingRequirement)

            }
            def sandbox = isSandbox()
            (1..num_hits).each {
                CreateHITRequest request = new CreateHITRequest();
                request.setMaxAssignments(1);
                request.setLifetimeInSeconds(hitLifetime * 60L);
                request.setAssignmentDurationInSeconds(assignmentLifetime * 60L);
                // 3 days
                request.setAutoApprovalDelayInSeconds(60 * 60 * 24 * 4)
                // Reward is a USD dollar amount - USD$0.20 in the example below
                request.setReward(trainingSet.training_payment as String);
                request.setTitle("Story Loom Training: " + trainingSet.name);
                request.setKeywords("qualifier, research, game");
                request.setDescription("This HIT will provide a qualifier so that you can participate in the Story Loom game");
                request.setQuestion(questionSample);
                if (qualifier) {
                    request.setQualificationRequirements(qualificationRequirements);
                }

                def result = getMturkClient().createHIT(request);
                def hiturl = "https://worker${sandbox ? "sandbox" : ""}.mturk.com/mturk/preview?groupId=" + result.getHIT().getHITTypeId()
                println(hiturl)
                log.debug(hiturl)
                trainingSet.HITId.add(result.getHIT().getHITId())
                trainingSet.HITTypeId.add(result.getHIT().getHITTypeId())
                trainingSet.save(flush: true)
            }
        }


    }


    def sendExperimentBonus(String assignmentId, def max_score, def total_score, def wait_time, def session_id, def worker_id) {

        SendBonusRequest req = new SendBonusRequest();
        Experiment exp = Session.get(session_id).exp
        def score_payment = exp.score
        def finished_payment = exp.completion
        Float payment
//        Float payment = 0.1
        if (total_score) {
            payment = (max_score / total_score) * score_payment + finished_payment + wait_time * exp.waiting
        } else {
            payment = finished_payment + wait_time * exp.waiting
        }

        req.setAssignmentId(assignmentId);
        req.setWorkerId(worker_id);
        req.setBonusAmount(payment.toString());
        req.setReason("finish experiment");
        SendBonusResult result = getMturkClient().sendBonus(req);
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

    def getHit(String HITId) {

        GetHITRequest hitreq = new GetHITRequest()
        hitreq.setHITId(HITId)
        GetHITResult hitresult = getMturkClient().getHIT(hitreq)
        return hitresult
    }

    def deleteHit(String HITId) {


        DeleteHITRequest dhr = new DeleteHITRequest()

        dhr.setHITId(HITId)
        getMturkClient().deleteHIT(dhr)

    }

    def updateExpirationForHit(Session session) {


        def HITIds = session.getHITId()
        List delete_HITIds = new ArrayList<>()
        for (String HITId : HITIds) {
            GetHITResult hitresult = getHit(HITId)
            println(hitresult.getHIT().getHITStatus())
            if (hitresult.getHIT().getHITStatus() == "Assignable") {
                UpdateExpirationForHITRequest req = new UpdateExpirationForHITRequest()

                req.setHITId(HITId)
                req.setExpireAt(new Date())
                getMturkClient().updateExpirationForHIT(req)
                deleteHit(HITId)
                delete_HITIds.add(HITId)

            }

        }
        for (String HITId : delete_HITIds) {
            if (session.HITId.contains(HITId)) {
                session.HITId.remove(HITId)
                session.save(flush: true)
            }

        }

    }


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
                def total_score = scores.sum()
                def max_score = scores.max()
                sendExperimentBonus(assignmentId, max_score as int, total_score as int, wait_time, session.id, worker_id)
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

}
