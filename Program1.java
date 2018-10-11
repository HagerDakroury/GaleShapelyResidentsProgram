/*
 * Name: <Hager Eldakroury>
 * EID: <hae386>
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Your solution goes in this class.
 * <p>
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 * <p>
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */
public class Program1 extends AbstractProgram1 {
    /**
     * Determines whether a candidate Matching represents a solution to the Stable Marriage problem.
     * Study the description of a Matching in the project documentation to help you with this.
     */
    @Override
    public boolean isStableMatching(Matching marriage) {

        int residentsNumber = marriage.getResidentCount();
        ArrayList<Integer> resident_matching = marriage.getResidentMatching();
        ArrayList<ArrayList<Integer>> hospital_preference = marriage.getHospitalPreference();
        ArrayList<ArrayList<Integer>> resident_preference = marriage.getResidentPreference();

        /*To check later if all slots are filled */
        int matchedResidents=0;

        /*loop through all residents*/
        for (int resident = 0; resident < residentsNumber; resident++) {
            if(resident_matching.get(resident)!=-1)
                matchedResidents++;
            int matchedHospital = resident_matching.get(resident);

            if (matchedHospital == -1)
                continue;

            /*loop through all residents before the selected resident in the matched hospital's preference list*/
            for (int residentToCompareIndex = 0; residentToCompareIndex < hospital_preference.get(matchedHospital).indexOf(resident); residentToCompareIndex++) {
                int residentToCompare = hospital_preference.get(matchedHospital).get(residentToCompareIndex);
                int matchedHospitalToCompare = resident_matching.get(residentToCompare);
                /*check for 1st instability*/
                if (matchedHospitalToCompare == -1)
                    return false;
                    /*check for 2nd instability*/
                else if (resident_preference.get(residentToCompare).indexOf(matchedHospital) < resident_preference.get(residentToCompare).indexOf(matchedHospitalToCompare))
                    return false;
            }
        }
        if(matchedResidents!=marriage.totalHospitalSlots())
            return false;
        return true;
    }

    /**
     * Determines a resident optimal solution to the Stable Marriage problem from the given input set.
     * Study the project description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMarriageBruteForce_residentoptimal(Matching marriage) {
        int residentsNumber = marriage.getResidentCount();
        int hospitalSlots = marriage.totalHospitalSlots();

        ArrayList<ArrayList<Integer>> validPartners = new ArrayList<ArrayList<Integer>>(0);
        ArrayList<Matching> stableMatchings = new ArrayList<Matching>();
        int count = 0;

        Permutation p = new Permutation(residentsNumber, hospitalSlots);
        Matching matching;
        int i=0;
        while ((matching = p.getNextMatching(marriage)) != null) {
            i++;

            if (isStableMatching(matching)) {
                stableMatchings.add(matching);
                validPartners.add(matching.getResidentMatching());
                count++;
            }
        }

        if (count == 1)
            return stableMatchings.get(0);

        validPartners = sortValidPartners(marriage, stableMatchings.size(), validPartners);

        return getBruteForceCorrectMatching(stableMatchings, validPartners);
    }

    /**
     * Determines a resident optimal solution to the Stable Marriage problem from the given input set.
     * Study the description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */

    /*Time Complexity O(n)+*/
    @Override
    public Matching stableMarriageGaleShapley_residentoptimal(Matching marriage) {

        int m = marriage.getHospitalCount();
        int n = marriage.getResidentCount();

        ArrayList<ArrayList<Integer>> hospital_preference = marriage.getHospitalPreference();
        ArrayList<ArrayList<Integer>> resident_preference = marriage.getResidentPreference();
        ArrayList<Integer> hospitalSlots = marriage.getHospitalSlots();
        ArrayList<Integer> residentMatching = new ArrayList<Integer>();
        arrlistInit(residentMatching, n, -1, false);                                                  //O(n)

        /*At first the resident can propose to all his list.
         Each time a proposal is made the hospital is removed from the list*/

        /*Trying to create a copy of the arraylist elements not copy of references*/
        ArrayList<ArrayList<Integer>> hospitalsToProposeTo = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < n; i++)                                                                    //O(n)
            hospitalsToProposeTo.add(new ArrayList<Integer>(resident_preference.get(i)));


        /*list of residents that still can propose(free and hasn't proposed to every hospital)*/
        ArrayList<Integer> proposing = new ArrayList<Integer>();
        arrlistInit(proposing, n, 0, true);                                                        //O(n)


        /*Keep track of each hospital matched residents*/
        ArrayList<ArrayList<Integer>> hospitalResidents = new ArrayList<ArrayList<Integer>>(0);
        for (int i = 0; i < m; i++)
            hospitalResidents.add(new ArrayList<Integer>(0));                               //O(m)

        /*Array list that holds the value of the lowest matched resident rank in each hospital
         * so each time a resident propose to a full hospital, the resident is swapped with the least ranked rmatched resident */
        ArrayList<Integer> lowestMatchedResidentRank = new ArrayList<Integer>();
        arrlistInit(lowestMatchedResidentRank, m, -1, false);                              //O(m)

        /*we enter the loop as long as some residents aren't done proposing to all hospitals yet O(mn*maximum no of spots)*/
        while (!proposing.isEmpty()) {

            /*Get the head of the proposing list*/
            for (int residentIndex = 0; residentIndex < proposing.size(); residentIndex++) {
                int resident = proposing.get(0);
                int hospital = 0;
                int hospitalIndex;
                /*Get the first hospital in the resident list which he hasn't proposed to yet, breaks if he can't no longer propose if matched*/
                for (hospitalIndex = 0; hospitalIndex < hospitalsToProposeTo.get(resident).size() && proposing.contains(resident); hospitalIndex++) {
                    hospital = hospitalsToProposeTo.get(resident).get(0);
                    int residentRank = hospital_preference.get(hospital).indexOf(resident);

                    /*hospital is full, loop through the matched residents and see if anyone can be kicked out*/
                    if (hospitalResidents.get(hospital).size() == hospitalSlots.get(hospital)) {

                        if (residentRank < lowestMatchedResidentRank.get(hospital)) {
                            /*1.Replace in hospitalResidents
                            * 2.Add/remove in resident-matching
                            * 3.Remove resident from the proposing list
                            * 4.Check if matched resident still has hospitals to propose to (if yes, add to proposing)
                            */
                            int lowestMatchedResident = hospital_preference.get(hospital).get(lowestMatchedResidentRank.get(hospital));

                            hospitalResidents.get(hospital).set(hospitalResidents.get(hospital).indexOf(lowestMatchedResident), resident);
                            residentMatching.set(lowestMatchedResident, -1);
                            residentMatching.set(resident, hospital);
                            proposing.remove(proposing.indexOf(resident));
                            if (!hospitalsToProposeTo.get(lowestMatchedResident).isEmpty()) {
                                proposing.add(lowestMatchedResident);
                            }

                            /*set the lowest rank
                             * TODO make it O(1)*/
                            int min = 0;
                            for (int i = 0; i < hospitalResidents.get(hospital).size(); i++) {
                                int tempRank = hospital_preference.get(hospital).indexOf(hospitalResidents.get(hospital).get(i));
                                if (tempRank > min)
                                    min = tempRank;
                            }
                            lowestMatchedResidentRank.set(hospital, min);

                        }
                    }

                    /*If there is available spot*/
                    else {
                        /*1.Add in hospitalResidents
                        * 2.Add in resident-matching
                        * 3.Set the lowest ranked resident
                        * 4.Remove resident from proposing list
                        */

                        /*Update the lowest rank*/
                        if (residentRank > lowestMatchedResidentRank.get(hospital))
                            lowestMatchedResidentRank.set(hospital, residentRank);

                        hospitalResidents.get(hospital).add(resident);
                        residentMatching.set(resident, hospital);
                        proposing.remove(proposing.indexOf(resident));
                    }

                    /*1. Remove hospital from resident's hospitalsToProposeTo
                     *2. If resident is matched or proposed to every possible hospital, remove resident from proposing list
                     */

                    hospitalsToProposeTo.get(resident).remove(hospitalsToProposeTo.get(resident).indexOf(hospital));
                    if (hospitalsToProposeTo.get(resident).size() == 0 && proposing.contains(resident))
                        proposing.remove(proposing.indexOf(resident));
                }
            }
        }

        marriage.setResidentMatching(residentMatching);
        return marriage;

    }

    /**
     * Determines a hospital optimal solution to the Stable Marriage problem from the given input set.
     * Study the description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMarriageGaleShapley_hospitaloptimal(Matching marriage) {
        int m = marriage.getHospitalCount();
        int n = marriage.getResidentCount();

        ArrayList<ArrayList<Integer>> hospital_preference = marriage.getHospitalPreference();
        ArrayList<ArrayList<Integer>> resident_preference = marriage.getResidentPreference();

        ArrayList<Integer> hospitalSlots = marriage.getHospitalSlots();

        ArrayList<Integer> residentMatching = new ArrayList<Integer>();
        arrlistInit(residentMatching, n, -1, false);

        /*list of residents that each hospital can propose to
         * Initially equals the hospital_preference list*/
        ArrayList<ArrayList<Integer>> residentsToProposeTo = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < m; i++)                                                                    //O(m)
            residentsToProposeTo.add(new ArrayList<Integer>(hospital_preference.get(i)));

        /*list of hospitals that still can propose(has free spots and hasn't proposed to every resident)*/
        ArrayList<Integer> proposing = new ArrayList<Integer>();
        arrlistInit(proposing, m, 0, true);                                                        //O(n)

        /*Keep track of each hospital matched residents*/
        ArrayList<ArrayList<Integer>> hospitalResidents = new ArrayList<ArrayList<Integer>>(0);
        for (int i = 0; i < m; i++)                                                              //O(m)
            hospitalResidents.add(new ArrayList<Integer>(0));

        /*Looping through each hospital in the proposing list
         * Even though some hospital may be added again in the proposing list, each hospital can propose at most once to each resident
         * So total running time of the loop will be O(m*n)*/
        while (!proposing.isEmpty()) {
            for (int hospitalIndex = 0; hospitalIndex < proposing.size(); hospitalIndex++) {
                /*Get the head of the proposing Arraylist*/
                int hospital = proposing.get(0);
                int residentIndex;
                int resident = 0;
                for (residentIndex = 0; residentIndex < residentsToProposeTo.get(hospital).size() && proposing.contains(hospital); hospitalIndex++) {
                    /*Get the next resident the hospital hasn't proposed to yed*/
                    resident = residentsToProposeTo.get(hospital).get(0);
                    int hospitalRank = resident_preference.get(resident).indexOf(hospital);

                    /*resident is matched, compare the hospitals rank*/
                    if (residentMatching.get(resident) != -1) {
                        int matchedHospital = residentMatching.get(resident);
                        int matchedHospitalRank = resident_preference.get(resident).indexOf(matchedHospital);
                        if (hospitalRank < matchedHospitalRank) {
                            /*1.Add/remove to hospitalResidents
                             * 2.Add in resident-matching
                             * 3.Add matchedHospital to the proposing list if it's not in it
                             */
                            hospitalResidents.get(hospital).add(resident);
                            hospitalResidents.get(matchedHospital).remove(hospitalResidents.get(matchedHospital).indexOf(resident));
                            residentMatching.set(resident, hospital);
                            if (!proposing.contains(matchedHospital))
                                proposing.add(matchedHospital);

                        }
                    }
                    /*resident is free, match with the hospital*/
                    else {
                        /*1.Add in hospitalResidents
                         * 2.Add in resident-matching
                         */
                        hospitalResidents.get(hospital).add(resident);
                        residentMatching.set(resident, hospital);
                    }

                    /*1.Remove resident from the hospital's list of residentsToProposeTo
                     * 2.If hospital's slots are full, remove hospital from the proposing list
                     */
                    residentsToProposeTo.get(hospital).remove(residentsToProposeTo.get(hospital).indexOf(resident));
                    if (hospitalResidents.get(hospital).size() >= hospitalSlots.get(hospital))
                        proposing.remove(proposing.indexOf(hospital));
                }
            }
        }
        marriage.setResidentMatching(residentMatching);
        return marriage;
    }

    private ArrayList<ArrayList<Integer>> sortValidPartners(Matching data, int validPartnersNo, ArrayList<ArrayList<Integer>> validPartners) {
        /*sorting each resident's valid partners using insertion sort*/
        for (int resident = 0; resident < validPartners.get(0).size(); resident++) {

            for (int partner = 1; partner < validPartnersNo; partner++) {
                int partnerRank = data.getResidentPreference().get(resident).indexOf(validPartners.get(partner).get(resident));
                int partnerValue = validPartners.get(partner).get(resident);
                if (partnerRank == -1)
                    partnerRank = 999999;
                int partnerToCompare = partner - 1;

                int partnerToCompareRank = data.getResidentPreference().get(resident).indexOf(validPartners.get(partnerToCompare).get(resident));
                if (partnerToCompareRank == -1)
                    partnerToCompareRank = 999999;
                while (partnerToCompareRank > partnerRank) {
                    validPartners.get(partnerToCompare + 1).set(resident, validPartners.get(partnerToCompare).get(resident));
                    partnerToCompare--;
                    if (partnerToCompare < 0)
                        break;
                    partnerToCompareRank = data.getResidentPreference().get(resident).indexOf(validPartners.get(partnerToCompare).get(resident));
                    if (partnerToCompareRank == -1)
                        partnerToCompareRank = 999999;
                }

                validPartners.get(partnerToCompare + 1).set(resident, partnerValue);
            }
        }
        return validPartners;
    }

    /*perfect matching will simply be the one with the first Arraylist*/
    private Matching getBruteForceCorrectMatching(ArrayList<Matching> stableMatchings, ArrayList<ArrayList<Integer>> validPartners) {
        for (int matching = 0; matching < stableMatchings.size(); matching++) {
            if (stableMatchings.get(matching).getResidentMatching().equals(validPartners.get(0)))
                return stableMatchings.get(matching);
        }
        return null;
    }

    private ArrayList<Integer> arrlistInit(ArrayList<Integer> arrlist, int n, int value, boolean proposing) {
        for (int i = 0; i < n; i++) {
            if (proposing)
                arrlist.add(i);
            else
                arrlist.add(value);
        }
        return arrlist;
    }

}
