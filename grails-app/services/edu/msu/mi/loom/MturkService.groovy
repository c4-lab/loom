package edu.msu.mi.loom

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.mturk.service.axis.RequesterService
import com.amazonaws.mturk.util.PropertiesClientConfig
import edu.msu.mi.mturk_utils.FilePropertiesConfig
//import com.amazonaws.mturk.service.axis.RequesterService
import com.amazonaws.services.mturk.AmazonMTurk
import com.amazonaws.services.mturk.AmazonMTurkClientBuilder
import com.amazonaws.services.mturk.model.*
import grails.transaction.Transactional
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors


@Transactional
class MturkService {

    AmazonMTurk client;
    def grailsApplication

    def getSandboxClient() {

//        FilePropertiesConfig config
        InputStream stream = this.class.classLoader.getResourceAsStream("global.mturk.properties")
        if (!stream) {
            println("Uh oh, can't find resource!")
        }

//        config = new FilePropertiesConfig(stream)
        Properties props = new Properties()
        props.load(stream);
        String AWS_ACCESS_KEY = props.getProperty("access_key")
        log.debug("Got access key ${AWS_ACCESS_KEY}")
        String AWS_SECRET_KEY = props.getProperty("secret_key")
        String SANDBOX_ENDPOINT = props.getProperty("sandbox_endpoint")
        String SIGNING_REGION = props.getProperty("signing_region")
        boolean sandbox = Boolean.parseBoolean(props.getProperty("sandbox"))
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
        AmazonMTurkClientBuilder builder = AmazonMTurkClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds));
        if(sandbox){
            println("**************** SANDBOX ENDPOINT SELECTED ****************")
            builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(SANDBOX_ENDPOINT, SIGNING_REGION));
        }else{
            println("**************** PRODUCTION ENDPOINT SELECTED ****************")
            String PRODUCTION_ENDPOINT = props.getProperty("production_endpoint")
            builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(PRODUCTION_ENDPOINT, SIGNING_REGION))
        }

        return builder.build();
    }

    def createQualification(TrainingSet ts, String description) {
        createQualification(TrainingSet.constructQualificationString(ts),description)
    }

    def createQualification(Simulation s, String description) {
        createQualification(Simulation.constructQualificationString(s),description)

    }

    def createQualification(Story story, String description) {
        createQualification(Story.constructQualificationString(story), description)
    }



    private setClient(AmazonMTurk clients) {
        client = clients;
    }


    def createQualification(String qualificationName, String description) {

        setClient(getSandboxClient());
        try {
            def s = searchQualificationTypeId(qualificationName)
            if(s && s.size()>0){
                log.warn("Qualification ${qualificationName} already exists: ${s}")

            }else{
                CreateQualificationTypeRequest createQualificationTypeRequest = new CreateQualificationTypeRequest();
                createQualificationTypeRequest.setName(qualificationName);
                createQualificationTypeRequest.setQualificationTypeStatus("Active");
                createQualificationTypeRequest.setDescription(description);
                createQualificationTypeRequest.setKeywords("loom,training,game");
                log.debug("Creating qualifiction ${createQualificationTypeRequest}")
                def r = client.createQualificationType(createQualificationTypeRequest);
                log.debug("Qual result is ${r}")
            }
        }catch (Exception e) {
            e.printStackTrace()
        }

    }

    def assignQualification(String workerId, String qualification, def value) {
        println(Math.floor(value).toInteger())
        value = Math.floor(value).toInteger()
        setClient(getSandboxClient())
        AssociateQualificationWithWorkerRequest aq = new AssociateQualificationWithWorkerRequest();
        def q = searchQualificationTypeId(qualification)
        log.debug("Attempt to assign ${q} to ${workerId}")
        aq.setQualificationTypeId(q);
        aq.setWorkerId(workerId);
        aq.setIntegerValue(value)
        client.associateQualificationWithWorker(aq);
    }


    def getQualificationScore(String qualificationId, String workerId){
        setClient(getSandboxClient());
        GetQualificationScoreRequest req = new GetQualificationScoreRequest()
        req.setQualificationTypeId(qualificationId)
        req.setWorkerId(workerId)
        GetQualificationScoreResult result = client.getQualificationScore(req)
        result.getQualification()
        return result

    }

    def searchQualificationTypeId(String qualificationType){
        setClient(getSandboxClient());
        ListQualificationTypesRequest lqtr = new ListQualificationTypesRequest();
        lqtr.setMustBeRequestable(true);
        lqtr.setMustBeOwnedByCaller(true)
        lqtr.setQuery(qualificationType);
        ListQualificationTypesResult result = client.listQualificationTypes(lqtr);
        List<QualificationType> s = result.getQualificationTypes();
        if (s.size()>0){
            log.debug("Found ${qualificationType} -> ${s}")
            return s.get(s.size()-1).getQualificationTypeId()
        }
        return null
    }


    def createExperimentHIT(Experiment exp, String sessionId) throws IOException {
        Session session = Session.get(sessionId)
        setClient(getSandboxClient());
        // QualificationRequirement: Locale IN (US, CA)
        String qualifier = exp.qualifier
        Collection<QualificationRequirement> qualificationRequirements = new ArrayList<>()
        //String questionSample = new String(Files.readAllBytes(Paths.get('grails-app/conf/my_question.xml')));
        InputStream is = this.class.classLoader.getResourceAsStream("my_question.xml")
        String questionSample = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        questionSample = questionSample.replace("goToThisLink","http://localhost:8080/loom/session/s/"+session.id.toString())
        if(qualifier){
            List qualifiers = qualifier.split(";")



            QualificationRequirement performanceRequirement = new QualificationRequirement();
            performanceRequirement.setQualificationTypeId(searchQualificationTypeId(Simulation.constructQualificationString(exp.training_set.simulations.first())));
            performanceRequirement.setComparator(Comparator.GreaterThanOrEqualTo);
            List<Integer> performanceValues = new ArrayList<>();
            performanceValues.add(qualifiers.get(4).split(">=")[1] as Integer);
            performanceRequirement.setIntegerValues(performanceValues)

            QualificationRequirement readingRequirement = new QualificationRequirement();
            readingRequirement.setQualificationTypeId(searchQualificationTypeId("loomreadings"));
            readingRequirement.setComparator(Comparator.GreaterThanOrEqualTo);
            List<Integer> readingValues = new ArrayList<>();
            readingValues.add(qualifiers.get(5).split(">=")[1] as Integer);
            readingRequirement.setIntegerValues(readingValues)

            QualificationRequirement vaccineRequirement = new QualificationRequirement();
            vaccineRequirement.setQualificationTypeId(searchQualificationTypeId("loomsurveys"));
            vaccineRequirement.setComparator(Comparator.In);
            List<Integer> vaccineValues = new ArrayList<>();
            (qualifiers.get(6).split("<=")[0]..qualifiers.get(6).split("<=")[2]).each { n ->
                vaccineValues.add(n as Integer);
            }
            QualificationRequirement numHitsRequirement = new QualificationRequirement();
            numHitsRequirement.setQualificationTypeId("3CNIZ8EIUVQZYD8YHMEU9ANVZY73BK");
            numHitsRequirement.setComparator(Comparator.GreaterThanOrEqualTo);
            List<Integer> numHitsValues = new ArrayList<>();
            numHitsValues.add(500);
            numHitsRequirement.setIntegerValues(numHitsValues)

            vaccineRequirement.setIntegerValues(vaccineValues)



            qualificationRequirements.add(performanceRequirement)
            qualificationRequirements.add(readingRequirement)
            qualificationRequirements.add(vaccineRequirement)
//            qualificationRequirements.add(numHitsRequirement)
        }
        QualificationRequirement localeRequirement = new QualificationRequirement();
        localeRequirement.setQualificationTypeId("00000000000000000071");
        localeRequirement.setComparator(Comparator.In);
        List<Locale> localeValues = new ArrayList<>();
        localeValues.add(new Locale().withCountry("US"));
        localeRequirement.setLocaleValues(localeValues);

        QualificationRequirement storyRequirement = new QualificationRequirement();
        storyRequirement.setQualificationTypeId(searchQualificationTypeId(Story.constructQualificationString(exp.story)));
        storyRequirement.setComparator(Comparator.DoesNotExist);




        QualificationRequirement approvalRateRequirement = new QualificationRequirement();
        approvalRateRequirement.setQualificationTypeId("000000000000000000L0");
        approvalRateRequirement.setComparator(Comparator.GreaterThanOrEqualTo);
        List<Integer> approvalRateValues = new ArrayList<>();
        approvalRateValues.add(98);
        approvalRateRequirement.setIntegerValues(approvalRateValues)
        qualificationRequirements.add(localeRequirement)
        qualificationRequirements.add(approvalRateRequirement)

        qualificationRequirements.add(storyRequirement)

        int max_HIT_num = exp.max_node
        List<String> hits = new ArrayList<>()
        List<String> hitTypes = new ArrayList<>()
        (1..max_HIT_num).each {
            CreateHITRequest request = new CreateHITRequest();
            request.setMaxAssignments(1);
            request.setLifetimeInSeconds(600L);
            request.setAssignmentDurationInSeconds(600L);
            // Reward is a USD dollar amount - USD$0.20 in the example below
            request.setReward(exp.accepting as String);
            request.setTitle("Loom Session HIT_"+session.id.toString()+"_"+it.toString());
            request.setKeywords("question, answer, research");
            request.setDescription("Answer a simple question");
            request.setQuestion(questionSample);
            request.setQualificationRequirements(qualificationRequirements);


            def result = client.createHIT(request);
            println("https://workersandbox.mturk.com/mturk/preview?groupId=" + result.getHIT().getHITTypeId())
            log.debug("https://workersandbox.mturk.com/mturk/preview?groupId=" + result.getHIT().getHITTypeId())
            hits.add(result.getHIT().getHITId())
            hitTypes.add(result.getHIT().getHITTypeId())

        }
        session.HITId = hits
        session.HITTypeId = hitTypes
        session.save()

    }

    def hasQualification(String workerId, String qualification){
        setClient(getSandboxClient());
        ListWorkersWithQualificationTypeRequest request = new ListWorkersWithQualificationTypeRequest()
        request.setQualificationTypeId(searchQualificationTypeId(qualification))
        ListWorkersWithQualificationTypeResult result = client.listWorkersWithQualificationType(request)
        List<Qualification> quals = result.getQualifications()
        boolean flag = false
        for(Qualification qual: quals){
            if(qual.getWorkerId() == workerId){
                flag = true
            }
        }
        return flag

    }

    def createTrainingHIT(TrainingSet trainingSet, int num_hits, String contextPath) throws IOException {

        log.debug("Got context path ${contextPath}")

        if(num_hits>0){
            setClient(getSandboxClient());
            // QualificationRequirement: Locale IN (US, CA)
            String qualifier = trainingSet.qualifier
            //String questionSample = new String(Files.readAllBytes(Paths.get('grails-app/conf/my_question.xml')))
            InputStream is = this.class.classLoader.getResourceAsStream("my_question.xml")
            String questionSample = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            questionSample = questionSample.replace("goToThisLink","${contextPath}/training/t/"+trainingSet.id.toString())
            QualificationRequirement localeRequirement = new QualificationRequirement();
            localeRequirement.setQualificationTypeId("00000000000000000071");
            localeRequirement.setComparator(Comparator.In);
            List<Locale> localeValues = new ArrayList<>();
            localeValues.add(new Locale().withCountry("US"));
            localeRequirement.setLocaleValues(localeValues);
            Collection<QualificationRequirement> qualificationRequirements = new ArrayList<>()
            qualificationRequirements.add(localeRequirement)

            if (qualifier){
                QualificationRequirement trainingRequirement = new QualificationRequirement();
                trainingRequirement.setQualificationTypeId(searchQualificationTypeId(TrainingSet.constructQualificationString(trainingSet)))
                trainingRequirement.setComparator(Comparator.DoesNotExist)
                qualificationRequirements.add(trainingRequirement)

            }
            (1..num_hits).each {
                CreateHITRequest request = new CreateHITRequest();
                request.setMaxAssignments(1);
                request.setLifetimeInSeconds(28800L);
                request.setAssignmentDurationInSeconds(3600L);
                // 3 days
                request.setAutoApprovalDelayInSeconds(259200)
                // Reward is a USD dollar amount - USD$0.20 in the example below
                request.setReward(trainingSet.training_payment as String);
                request.setTitle("Story Loom Training: "+trainingSet.name);
                request.setKeywords("qualifier, research, game");
                request.setDescription("This HIT will provide a qualifier so that you can participate in the Story Loom game");
                request.setQuestion(questionSample);
                if(qualifier){
                    request.setQualificationRequirements(qualificationRequirements);
                }

                def result = client.createHIT(request);
                println("https://workersandbox.mturk.com/mturk/preview?groupId=" + result.getHIT().getHITTypeId())
                log.debug("https://workersandbox.mturk.com/mturk/preview?groupId=" + result.getHIT().getHITTypeId())
                trainingSet.HITId.add(result.getHIT().getHITId())
                trainingSet.HITTypeId.add( result.getHIT().getHITTypeId())
                trainingSet.save(flush: true)
            }
        }


    }



    def sendExperimentBonus(String assignmentId, def max_score, def total_score, def wait_time, def session_id, def worker_id){
        setClient(getSandboxClient())
        SendBonusRequest req = new SendBonusRequest();
        Experiment exp = Session.get(session_id).exp
        def score_payment = exp.score
        def finished_payment = exp.completion
        Float payment
//        Float payment = 0.1
        if(total_score){
            payment = (max_score/total_score) * score_payment + finished_payment + wait_time * exp.waiting
        }else{
            payment = finished_payment + wait_time * exp.waiting
        }

        req.setAssignmentId(assignmentId);
        req.setWorkerId(worker_id);
        req.setBonusAmount(payment.toString());
        req.setReason("finish experiment");
        SendBonusResult result = client.sendBonus(req);
    }

//    def sendExperimentWaitingBonus(String assignmentId, def wait_time, def session_id){
//        SendBonusRequest req = new SendBonusRequest();
//        def waiting = Session.get(session_id).exp.waiting
//        float payment = wait_time * waiting
//        req.setAssignmentId(assignmentId);
//        req.setWorkerId(getWorkerId(assignmentId));
//        req.setBonusAmount(payment.toString());
//        req.setReason("finish experiment");
//        SendBonusResult result = client.sendBonus(req);
//    }

//    def blockWorker(String workerId){
//        CreateWorkerBlockRequest req = new CreateWorkerBlockRequest()
//        req.setWorkerId(workerId)
//        CreateWorkerBlockResult result = client.createWorkerBlock(req)
//    }

    def getWorkerId(String assignmentId){
        setClient(getSandboxClient())
        GetAssignmentRequest req = new GetAssignmentRequest()
        req.setAssignmentId(assignmentId)
        GetAssignmentResult result = client.getAssignment(req)
        String workerId = result.getAssignment().getWorkerId()
        return workerId
    }

    def listAssighmentsForHIT(String HITId){
        setClient(getSandboxClient())
        ListAssignmentsForHITRequest req = new ListAssignmentsForHITRequest()
        req.setHITId(HITId)
        ListAssignmentsForHITResult result = client.listAssignmentsForHIT(req)
        if (result.getAssignments()){
            return result.getAssignments().get(0).getAssignmentId()
        }
        return null
    }

    def getHit(String HITId){
        setClient(getSandboxClient())
        GetHITRequest hitreq = new GetHITRequest()
        hitreq.setHITId(HITId)
        GetHITResult hitresult = client.getHIT(hitreq)
        return hitresult
    }

    def deleteHit(String HITId){

        setClient(getSandboxClient());
        DeleteHITRequest dhr = new DeleteHITRequest()

        dhr.setHITId(HITId)
        client.deleteHIT(dhr)

    }

    def updateExpirationForHit(Session session){
        setClient(getSandboxClient())

        def HITIds = session.getHITId()
        List delete_HITIds = new ArrayList<>()
        for(String HITId: HITIds){
            GetHITResult hitresult = getHit(HITId)
            println(hitresult.getHIT().getHITStatus())
            if (hitresult.getHIT().getHITStatus() == "Assignable"){
                UpdateExpirationForHITRequest req = new UpdateExpirationForHITRequest()

                req.setHITId(HITId)
                req.setExpireAt(new Date())
                client.updateExpirationForHIT(req)
                deleteHit(HITId)
                delete_HITIds.add(HITId)

            }

        }
        for(String HITId: delete_HITIds){
            if (session.HITId.contains(HITId)){
                session.HITId.remove(HITId)
                session.save(flush: true)
            }

        }

    }


    def check_session_payable(Session session){
        setClient(getSandboxClient())
        int count = UserSession.countBySession(session)
        def HITIds = session.getHITId()
        // #submitted+#approved
        int total = 0
        int payable = 0
        List payableHIT = new ArrayList()
        for(String HITId: HITIds){

            String assignmentId = listAssighmentsForHIT(HITId)
            if(assignmentId){
                GetAssignmentRequest req = new GetAssignmentRequest()
                req.setAssignmentId(assignmentId)
                GetAssignmentResult result = client.getAssignment(req)
                String status = result.getAssignment().getAssignmentStatus()
                if (status == "Submitted" || (status == "Approved" && listBonus(HITId).size() == 0)){
                    payable += 1
                    payableHIT.add(HITId)

                }
                if (status == "Submitted" || status == "Approved"){
                    total += 1

                }
            }

        }
        session.paid = total-payable
        session.total = total
        session.save(flush: true)

            return [payableHIT, total-payable, total, count]
    }

    def check_trainingset_payable(TrainingSet trainingSet){
        setClient(getSandboxClient())
        def HITIds = trainingSet.getHITId()
        int total = 0
        int payable = 0
        List payableHIT = new ArrayList()
        for(String HITId: HITIds){

            String assignmentId = listAssighmentsForHIT(HITId)
            if(assignmentId){
                GetAssignmentRequest req = new GetAssignmentRequest()
                req.setAssignmentId(assignmentId)
                GetAssignmentResult result = client.getAssignment(req)
                String status = result.getAssignment().getAssignmentStatus()
                if (status == "Submitted"){
                    payable += 1
                    payableHIT.add(HITId)

                }
                if (status == "Submitted" || status == "Approved"){
                    total += 1

                }
            }

        }
        trainingSet.paid = total-payable
        trainingSet.total = total
        trainingSet.save(flush: true)

        return [payableHIT, total-payable, total]
    }

    def listBonus(String HITId){
        setClient(getSandboxClient())
        ListBonusPaymentsRequest req = new ListBonusPaymentsRequest()
        req.setHITId(HITId)
        ListBonusPaymentsResult result = client.listBonusPayments(req)
        return result.getBonusPayments()
    }

    def pay_session_HIT(Session session){
        setClient(getSandboxClient())
        def (payableHIT, paid, total, count) = check_session_payable(session)
        for(String HITId: payableHIT){
            String assignmentId = listAssighmentsForHIT(HITId)
            GetAssignmentRequest req = new GetAssignmentRequest()
            req.setAssignmentId(assignmentId)
            GetAssignmentResult result = client.getAssignment(req)
            String state = result.getAssignment().getAssignmentStatus()
            if (state == "Submitted"){
                ApproveAssignmentRequest areq = new ApproveAssignmentRequest()
                areq.setAssignmentId(assignmentId)
                client.approveAssignment(areq)
            }

            String worker_id = getWorkerId(assignmentId)
            User user = User.findByTurkerId(worker_id)
            UserSession us = UserSession.findBySessionAndUser(session, user)
            if (us){
                int wait_time = us?.wait_time?:0
                List scores = UserRoundStory.findAllBySessionAndUserAlias(session,us    .userAlias).sort {it.round}.score
                def total_score = scores.sum()
                def max_score = scores.max()
                sendExperimentBonus(assignmentId, max_score as int, total_score as int, wait_time, session.id, worker_id)
            }else{
                sendExperimentBonus(assignmentId, 0, 0, 0, session.id, worker_id)
            }


        }
    }

    def pay_trainingset_HIT(TrainingSet trainingSet){
        setClient(getSandboxClient())
        def (payableHIT, paid, total) = check_trainingset_payable(trainingSet)
        for(String HITId: payableHIT){
            String assignmentId = listAssighmentsForHIT(HITId)
            GetAssignmentRequest req = new GetAssignmentRequest()
            req.setAssignmentId(assignmentId)
            GetAssignmentResult result = client.getAssignment(req)
            String state = result.getAssignment().getAssignmentStatus()
            if (state == "Submitted"){
                ApproveAssignmentRequest areq = new ApproveAssignmentRequest()
                areq.setAssignmentId(assignmentId)
                client.approveAssignment(areq)
            }
        }
    }

    def rejectAssignment(String assignmentId){
        setClient(getSandboxClient())
        RejectAssignmentRequest request = new RejectAssignmentRequest()
        request.setAssignmentId(assignmentId)
        client.rejectAssignment(request)
    }

}
