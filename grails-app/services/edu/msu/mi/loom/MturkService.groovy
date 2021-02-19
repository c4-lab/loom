package edu.msu.mi.loom

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
//import com.amazonaws.mturk.service.axis.RequesterService
import com.amazonaws.services.mturk.AmazonMTurk
import com.amazonaws.services.mturk.AmazonMTurkClientBuilder
import com.amazonaws.services.mturk.model.*
import grails.transaction.Transactional

import java.nio.file.Files
import java.nio.file.Paths

@Transactional
class MturkService {
    String SANDBOX_ENDPOINT = "mturk-requester-sandbox.us-east-1.amazonaws.com";
    String PROD_ENDPOINT = "https://mturk-requester.us-east-1.amazonaws.com";
    String SIGNING_REGION = "us-east-1";
    String AWS_ACCESS_KEY = "";
    String AWS_SECRET_KEY = "";
    //test
    def AmazonMTurk client;

    def getProductionClient() {
        AmazonMTurkClientBuilder builder = AmazonMTurkClientBuilder.standard();
        builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(PRODUCTION_ENDPOINT, SIGNING_REGION));
        return builder.build();
    }

    def getSandboxClient() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
        AmazonMTurkClientBuilder builder = AmazonMTurkClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds));

        builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(SANDBOX_ENDPOINT, SIGNING_REGION));
        return builder.build();
    }

    def createQualification(TrainingSet ts, String description) {
        createQualification(TrainingSet.constructQualificationString(ts),description)
    }

    def createQualification(Story story, String description) {
        createQualification("Story"+story.id.toString(), description)
    }



    private setClient(AmazonMTurk clients) {
        client = clients;
    }


    def createQualification(String qualificationName, String description) {
        if(qualificationName=="loomnumHits"){
            println()
        }
        setClient(getSandboxClient());
        try {
            def s = searchQualificationTypeId(qualificationName)
            if(s && s.size()>0){
                println("Qualification already exists")

            }else{
                CreateQualificationTypeRequest createQualificationTypeRequest = new CreateQualificationTypeRequest();
                createQualificationTypeRequest.setName(qualificationName);
                createQualificationTypeRequest.setQualificationTypeStatus("Active");
                createQualificationTypeRequest.setDescription(description);
                createQualificationTypeRequest.setKeywords("loom,training,game");
                client.createQualificationType(createQualificationTypeRequest);
            }
        }catch (Exception e) {
            e.printStackTrace()
        }

    }

    def assignQualification(String workerId) {

        setClient(getSandboxClient());
        AssociateQualificationWithWorkerRequest aq = new AssociateQualificationWithWorkerRequest();
        aq.setQualificationTypeId(searchQualificationTypeId("loomnumHits"));
        aq.setWorkerId(workerId);
        aq.setIntegerValue(500)
        client.associateQualificationWithWorker(aq);

        AssociateQualificationWithWorkerRequest aq1 = new AssociateQualificationWithWorkerRequest();

        aq1.setQualificationTypeId(searchQualificationTypeId("loomperformances"));
        aq1.setWorkerId(workerId);
        aq1.setIntegerValue(1)
        client.associateQualificationWithWorker(aq1);

        AssociateQualificationWithWorkerRequest aq2 = new AssociateQualificationWithWorkerRequest();
        aq2.setQualificationTypeId(searchQualificationTypeId("loomreadings"));
        aq2.setWorkerId(workerId);
        aq2.setIntegerValue(1)
        client.associateQualificationWithWorker(aq2);

        AssociateQualificationWithWorkerRequest aq3 = new AssociateQualificationWithWorkerRequest();
        aq3.setQualificationTypeId(searchQualificationTypeId("loomvaccines"));
        aq3.setWorkerId(workerId);
        aq3.setIntegerValue(1)
        client.associateQualificationWithWorker(aq3);






//        try {
//            if(qualificationType){
//                AssociateQualificationWithWorkerRequest aq = new AssociateQualificationWithWorkerRequest();
//                aq.setQualificationTypeId(qualificationType);
//                aq.setWorkerId(workerId);
//                aq.setIntegerValue()
//                client.associateQualificationWithWorker(aq);
//            }else{
//                def s = searchQualificationTypeId(qualificationName)
//                if(s && s.size()!=1){
//                    println("Could not identify a unique qualification type")
//                }else if (s && s.size()==1){
//
//                    AssociateQualificationWithWorkerRequest aq = new AssociateQualificationWithWorkerRequest();
//                    aq.setQualificationTypeId(s.get(0).getQualificationTypeId());
//                    aq.setWorkerId(workerId);
//                    client.associateQualificationWithWorker(aq);
//                }
//            }
//
//        }catch (Exception e) {
//            e.printStackTrace()
//        }
    }



    def searchQualificationTypeId(String qualificationType){
        ListQualificationTypesRequest lqtr = new ListQualificationTypesRequest();
        lqtr.setMustBeRequestable(true);
        lqtr.setQuery(qualificationType);
        ListQualificationTypesResult result = client.listQualificationTypes(lqtr);
        List<QualificationType> s = result.getQualificationTypes();
        if (s.size()>0){
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
        String questionSample = new String(Files.readAllBytes(Paths.get('grails-app/conf/my_question.xml')));

        if(qualifier){
            List qualifiers = qualifier.split(";")

            QualificationRequirement localeRequirement = new QualificationRequirement();
            localeRequirement.setQualificationTypeId("00000000000000000071");
            localeRequirement.setComparator(Comparator.In);
            List<Locale> localeValues = new ArrayList<>();
            localeValues.add(new Locale().withCountry("US"));
            localeRequirement.setLocaleValues(localeValues);

            QualificationRequirement storyRequirement = new QualificationRequirement();
//            if(!searchQualificationTypeId("Story"+exp.story.id.toString())){
//                createQualification(exp.story, "loom story")
//            }
            storyRequirement.setQualificationTypeId(searchQualificationTypeId("Story"+exp.story.id.toString()));
            storyRequirement.setComparator(Comparator.DoesNotExist);


            QualificationRequirement numHitsRequirement = new QualificationRequirement();
            numHitsRequirement.setQualificationTypeId(searchQualificationTypeId("loomnumHits"));
            numHitsRequirement.setComparator(Comparator.GreaterThanOrEqualTo);
            List<Integer> numHitsValues = new ArrayList<>();
            numHitsValues.add(500);
            numHitsRequirement.setIntegerValues(numHitsValues)

            QualificationRequirement approvalRateRequirement = new QualificationRequirement();
            approvalRateRequirement.setQualificationTypeId("000000000000000000L0");
            approvalRateRequirement.setComparator(Comparator.GreaterThanOrEqualTo);
            List<Integer> approvalRateValues = new ArrayList<>();
            approvalRateValues.add(98);
            approvalRateRequirement.setIntegerValues(approvalRateValues)

            QualificationRequirement performanceRequirement = new QualificationRequirement();
//            if(!searchQualificationTypeId("loomperformance")){
//                createQualification(exp.story, "performance score")
//            }
            performanceRequirement.setQualificationTypeId(searchQualificationTypeId("loomperformances"));
            performanceRequirement.setComparator(Comparator.GreaterThanOrEqualTo);
            List<Integer> performanceValues = new ArrayList<>();
            performanceValues.add(qualifiers.get(4).split(">=")[1] as Integer);
            performanceRequirement.setIntegerValues(performanceValues)

            QualificationRequirement readingRequirement = new QualificationRequirement();
//            if(!searchQualificationTypeId("loomreading")){
//                createQualification(exp.story, "reading score")
//            }
            readingRequirement.setQualificationTypeId(searchQualificationTypeId("loomreadings"));
            readingRequirement.setComparator(Comparator.GreaterThanOrEqualTo);
            List<Integer> readingValues = new ArrayList<>();
            readingValues.add(qualifiers.get(5).split(">=")[1] as Integer);
            readingRequirement.setIntegerValues(readingValues)

            QualificationRequirement vaccineRequirement = new QualificationRequirement();
//            if(!searchQualificationTypeId("loomvaccine")){
//                createQualification(exp.story, "vaccine score")
//            }
            vaccineRequirement.setQualificationTypeId(searchQualificationTypeId("loomvaccines"));
            vaccineRequirement.setComparator(Comparator.In);
            List<Integer> vaccineValues = new ArrayList<>();
            (qualifiers.get(6).split("<=")[0]..qualifiers.get(6).split("<=")[2]).each { n ->
                vaccineValues.add(n as Integer);
            }


            vaccineRequirement.setIntegerValues(vaccineValues)


            qualificationRequirements.add(localeRequirement)
            qualificationRequirements.add(storyRequirement)
            qualificationRequirements.add(approvalRateRequirement)
            qualificationRequirements.add(numHitsRequirement)
            qualificationRequirements.add(performanceRequirement)
            qualificationRequirements.add(readingRequirement)
            qualificationRequirements.add(vaccineRequirement)
        }



        int max_HIT_num = exp.max_node
        List<String> hits = new ArrayList<>()
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
            if(qualifier){
                request.setQualificationRequirements(qualificationRequirements);
            }

            def result = client.createHIT(request);
            println("https://workersandbox.mturk.com/mturk/preview?groupId=" + result.getHIT().getHITTypeId())
            log.debug("https://workersandbox.mturk.com/mturk/preview?groupId=" + result.getHIT().getHITTypeId())
            hits.add(result.getHIT().getHITId())

        }
        session.HITId = hits
        session.save()

    }

    def createTrainingHIT(TrainingSet trainingSet) throws IOException {

        setClient(getSandboxClient());
        // QualificationRequirement: Locale IN (US, CA)
        String qualifier = trainingSet.qualifier
        String questionSample = new String(Files.readAllBytes(Paths.get('grails-app/conf/my_question.xml')));
        QualificationRequirement localeRequirement = new QualificationRequirement();
        localeRequirement.setQualificationTypeId("00000000000000000071");
        localeRequirement.setComparator(Comparator.In);
        List<Locale> localeValues = new ArrayList<>();
        localeValues.add(new Locale().withCountry("US"));
        localeRequirement.setLocaleValues(localeValues);
        Collection<QualificationRequirement> qualificationRequirements = new ArrayList<>()
        qualificationRequirements.add(localeRequirement)

        if (qualifier){
            List qualifiers = qualifier.split(";")
            println("asdfsdfffsdfdf")
            println(qualifiers)
            if(qualifiers.size()>0 && qualifiers.get(0)){
                QualificationRequirement performanceRequirement = new QualificationRequirement();
                performanceRequirement.setQualificationTypeId(searchQualificationTypeId("loomperformances"));
                performanceRequirement.setComparator(Comparator.LessThan);
                List<Integer> performanceValues = new ArrayList<>();
                performanceValues.add(qualifiers.get(0).split(">=")[1] as Integer);
                performanceRequirement.setIntegerValues(performanceValues)
                qualificationRequirements.add(performanceRequirement)

            }

            if (qualifiers.size()>1 && qualifiers.get(1)){
                QualificationRequirement readingRequirement = new QualificationRequirement();
                readingRequirement.setQualificationTypeId(searchQualificationTypeId("loomreadings"));
                readingRequirement.setComparator(Comparator.LessThan);
                List<Integer> readingValues = new ArrayList<>();
                readingValues.add(qualifiers.get(1).split(">=")[1] as Integer);
                readingRequirement.setIntegerValues(readingValues)
                qualificationRequirements.add(readingRequirement)
            }

            if (qualifiers.size()>2 && qualifiers.get(2)){
                QualificationRequirement vaccineRequirement = new QualificationRequirement();
//            if(!searchQualificationTypeId("loomvaccine")){
//                createQualification(exp.story, "vaccine score")
//            }
                vaccineRequirement.setQualificationTypeId(searchQualificationTypeId("loomvaccines"));
                vaccineRequirement.setComparator(Comparator.In);
                List<Integer> vaccineValues = new ArrayList<>();
                (qualifiers.get(6).split("<=")[0]..qualifiers.get(6).split("<=")[2]).each { n ->
                    vaccineValues.add(n as Integer);
                }


                vaccineRequirement.setIntegerValues(vaccineValues)
                qualificationRequirements.add(vaccineRequirement)
            }
        }


        int HIT_num = trainingSet.HIT_num
        (1..HIT_num).each {
            CreateHITRequest request = new CreateHITRequest();
            request.setMaxAssignments(1);
            request.setLifetimeInSeconds(60L);
            request.setAssignmentDurationInSeconds(600L);
            // 3 days
            request.setAutoApprovalDelayInSeconds(259200)
            // Reward is a USD dollar amount - USD$0.20 in the example below
            request.setReward(trainingSet.training_payment as String);
            request.setTitle("Loom Training HIT_"+trainingSet.id.toString()+"_"+it.toString());
            request.setKeywords("question, answer, research");
            request.setDescription("Answer a simple question");
            request.setQuestion(questionSample);
            if(qualifier){
                request.setQualificationRequirements(qualificationRequirements);
            }

            def result = client.createHIT(request);
            println("https://workersandbox.mturk.com/mturk/preview?groupId=" + result.getHIT().getHITTypeId())
            log.debug("https://workersandbox.mturk.com/mturk/preview?groupId=" + result.getHIT().getHITTypeId())
            trainingSet.HITId.add(result.getHIT().getHITId())
            trainingSet.save(flush: true)
        }

    }

    def deleteHit(String HITId){

//        List<String> HITIds = session.getHITId()

//        for(String HITId: HITIds){
//            GetHITResult hitresult = getHit(HITId)
        setClient(getSandboxClient());
        DeleteHITRequest dhr = new DeleteHITRequest()

        dhr.setHITId(HITId)
        client.deleteHIT(dhr)

//                try {
//                    DeleteHITResult result = client.deleteHIT(dhr)
////                    session.HITId.remove(HITId)
////                    session.save(flush: true)
//
//                }catch (Exception e) {
//                    e.printStackTrace()
//                }



//        }

    }

    def sendExperimentBonus(String assignmentId, def max_score, def total_score, def wait_time, def session_id, def worker_id){
        setClient(getSandboxClient())
        SendBonusRequest req = new SendBonusRequest();
        Experiment exp = Session.get(session_id).exp
        def score_payment = exp.score
        def finished_payment = exp.completion
        float payment = (max_score/total_score) * score_payment + finished_payment + wait_time * exp.waiting
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

    def completedAssignment(Session session){
        setClient(getSandboxClient())
        int count = 0
        List HITIds = session.HITId
        int total = 0
        for(String hitId: HITIds){
//            GetHITRequest hitreq = new GetHITRequest()
//            hitreq.setHITId(hitId)
//            GetHITResult hitresult = client.getHIT(hitreq)
            GetHITResult hitresult = getHit(hitId)
            total = total + hitresult.getHIT().getNumberOfAssignmentsCompleted()

            ListBonusPaymentsRequest req = new ListBonusPaymentsRequest()
            req.setHITId(hitId)
            ListBonusPaymentsResult result = client.listBonusPayments(req)
            if(result.getBonusPayments().size()>0){
                count = count+1
            }

        }
        return count.toString()+"/"+total.toString()


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
//                try {
                client.updateExpirationForHIT(req)
                deleteHit(HITId)
                delete_HITIds.add(HITId)
//



//                }catch (Exception e) {
//                    e.printStackTrace()
//                }
            }



        }
        for(String HITId: delete_HITIds){
            if (session.HITId.contains(HITId)){
                session.HITId.remove(HITId)
                session.save(flush: true)
            }

        }

//        session.HITId = new ArrayList<>()
//        session.save(flush: true)


    }



    def check_payable(Session session){
        setClient(getSandboxClient())
        if (session.state == Session.State.CANCEL || session.state == Session.State.FINISHED){
            def HITIds = session.getHITId()
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
                }

                total += 1


            }
            return [payableHIT, payable.toString()+"/"+total.toString()]
        }
        return [null, null]

    }

    def listBonus(String HITId){
        setClient(getSandboxClient())
        ListBonusPaymentsRequest req = new ListBonusPaymentsRequest()
        req.setHITId(HITId)
        ListBonusPaymentsResult result = client.listBonusPayments(req)
        return result.getBonusPayments()
    }

    def pay_HIT(Session session){
        setClient(getSandboxClient())
        def (payableHIT, payment_status) = check_payable(session)
//        def HITIds = session.getHITId()
        for(String HITId: payableHIT){
            String assignmentId = listAssighmentsForHIT(HITId)
            GetAssignmentRequest req = new GetAssignmentRequest()
            req.setAssignmentId(assignmentId)
            GetAssignmentResult result = client.getAssignment(req)
            String state = result.getAssignment().getAssignmentStatus()
//            if (status == "Submitted" || (status == "Approved" && listBonus(HITId).size() == 0)){
                if (state == "Submitted"){
                    ApproveAssignmentRequest areq = new ApproveAssignmentRequest()
                    areq.setAssignmentId(assignmentId)
                    client.approveAssignment(areq)
                }


                String worker_id = getWorkerId(assignmentId)
                User user = User.findByTurkerId(worker_id)
                UserSession us = UserSession.findBySessionAndUser(session, user)
                int wait_time = us.wait_time
                List scores = UserRoundStory.findAllBySessionAndUserAlias(session,us.userAlias).sort {it.round}.score
                def total_score = scores.sum()
                def max_score = scores.max()
                sendExperimentBonus(assignmentId, max_score as int, total_score as int, wait_time, session.id, worker_id)

//            }


        }
    }


}
