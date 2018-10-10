/*
 * Name: <Hager Eldakroury>
 * EID: <hae386>
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Your solution goes in this class.
 * 
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 * 
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

        int residentsNumber=marriage.getResidentCount();
        ArrayList<Integer> resident_matching=marriage.getResidentMatching();
        ArrayList<ArrayList<Integer>> hospital_preference=marriage.getHospitalPreference();
        ArrayList<ArrayList<Integer>> resident_preference=marriage.getResidentPreference();

        /*loop through all residents*/
        for(int resident=0;resident<residentsNumber;resident++){
            int matchedHospital=resident_matching.get(resident);

            if(matchedHospital==-1)
                continue;

            /*loop through all residents before the selected resident in the matched hospital's preference list*/
            for(int residentToCompareIndex=0; residentToCompareIndex<hospital_preference.get(matchedHospital).indexOf(resident);residentToCompareIndex++){
                int residentToCompare=hospital_preference.get(matchedHospital).get(residentToCompareIndex);
                int matchedHospitalToCompare=resident_matching.get(residentToCompare);
                /*check for 1st instability*/
                if(matchedHospitalToCompare==-1)
                    return false;
                /*check for 2nd instability*/
                else if(resident_preference.get(residentToCompare).indexOf(matchedHospital)<resident_preference.get(residentToCompare).indexOf(matchedHospitalToCompare))
                    return false;
            }
        }
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

        ArrayList<ArrayList<Integer>> validPartners=new ArrayList<ArrayList<Integer>>(0);
        ArrayList<Matching> stableMatchings=new ArrayList<Matching>();
        int count=0;

        Permutation p = new Permutation(residentsNumber, hospitalSlots);
        Matching matching;
        while ((matching = p.getNextMatching(marriage)) != null) {
            if (isStableMatching(matching)) {
                stableMatchings.add(matching);
                validPartners.add(matching.getResidentMatching());
                count++;
            }
        }

        if(count==1)
            return stableMatchings.get(0);

        validPartners=sortValidPartners(marriage,stableMatchings.size(),validPartners);

        return getBruteForceCorrectMatching(stableMatchings,validPartners);
        }

    /**
     * Determines a resident optimal solution to the Stable Marriage problem from the given input set.
     * Study the description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMarriageGaleShapley_residentoptimal(Matching marriage) {

        int m=marriage.getHospitalCount();
        int n=marriage.getResidentCount();
        ArrayList<ArrayList<Integer>> hospital_preference=marriage.getHospitalPreference();
        ArrayList<ArrayList<Integer>> resident_preference=marriage.getResidentPreference();
        ArrayList<Integer> hospitalSlots=marriage.getHospitalSlots();
        ArrayList<Integer> residentMatching=new ArrayList<Integer>();
        arrlistInit(residentMatching,n,-1,false);

        /*At first the resident can propose to all his list.
         Each time a proposal is made the hospital is removed from the list*/
        ArrayList<ArrayList<Integer>> hospitalsToProposeTo=resident_preference;

        /*list of residents that still can propose(free and hasn't proposed to every hospital)*/
        ArrayList<Integer> proposing=new ArrayList<>();
        arrlistInit(proposing,n,0,true);


        ArrayList<ArrayList<Integer>> hospitalResidents=new ArrayList<>(m);

        /*we enter the loop as long as some residents aren't done proposing yet O(mn)*/
        while(!proposing.isEmpty()) {

            for (int residentIndex = 0; residentIndex <proposing.size(); residentIndex++) {
                int resident=proposing.get(residentIndex);
                int hospital = 0;
                int hospitalIndex;
                while (residentMatching.get(resident) != -1 ) {
                    /*Get the first hospital in the resident list which he hasn't proposed to yet*/
                    for (hospitalIndex = 0; hospitalIndex < hospitalsToProposeTo.get(resident).size(); hospitalIndex++) {
                        hospital = hospitalsToProposeTo.get(resident).get(hospitalIndex);
                        /*hospital is full, loop through the matched residents and see if anyone can be kicked out*/
                        if (hospitalSlots.get(hospital) <= 0) {
                            ArrayList<Integer> matchedResidents = new ArrayList<Integer>();
                            for (int matchedResidentIndex = 0; matchedResidentIndex < hospitalResidents.get(hospital).size(); matchedResidentIndex++) {
                                int residentRank = hospital_preference.get(hospital).indexOf(resident);
                                int matchedResident =hospitalResidents.get(hospital).get(matchedResidentIndex);
                                int matchedResidentRank = resident_preference.get(resident).indexOf(matchedHospital);
                            }


                            if (matchedHospitalRank > hospitalRank)
                                break;
                            else {
                                residentMatching.set(resident, hospital);
                                hospitalSlots.set(hospital, hospitalSlots.get(hospital) - 1);
                                hospitalSlots.set(matchedHospital, hospitalSlots.get(matchedHospital) + 1);
                            }
                        } else {
                            residentMatching.set(resident, hospital);
                            hospitalSlots.set(hospital, hospitalSlots.get(hospital) - 1);
                        }
                    }

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
     int m=marriage.getHospitalCount();
     int n=marriage.getResidentCount();
     int totalSlots=marriage.totalHospitalSlots();
     ArrayList<ArrayList<Integer>> hospital_preference=marriage.getHospitalPreference();
     ArrayList<ArrayList<Integer>> resident_preference=marriage.getResidentPreference();
     ArrayList<Integer> hospitalSlots=marriage.getHospitalSlots();
     ArrayList<Integer> residentMatching=new ArrayList<Integer>();
     arrlistInit(residentMatching,n,-1);
     while(totalSlots>0)
     for(int hospital=0;hospital<m;hospital++) {
         int resident = 0;
         int residentIndex = 0;
         while (hospitalSlots.get(hospital) > 0 ) {
             resident = hospital_preference.get(hospital).get(residentIndex);
             if (residentMatching.get(resident) == -1) {
                 residentMatching.set(resident, hospital);
                 hospitalSlots.set(hospital, hospitalSlots.get(hospital) - 1);
                 totalSlots--;
             } else {
                 int hospitalRank = resident_preference.get(resident).indexOf(hospital);
                 int matchedHospital = residentMatching.get(resident);
                 int matchedHospitalRank = resident_preference.get(resident).indexOf(matchedHospital);
                 if (matchedHospitalRank > hospitalRank)
                     break;
                 else {
                     residentMatching.set(resident, hospital);
                     hospitalSlots.set(hospital, hospitalSlots.get(hospital) - 1);
                     hospitalSlots.set(matchedHospital, hospitalSlots.get(matchedHospital) + 1);
                 }
             }
         }
     }

     marriage.setResidentMatching(residentMatching);
     return marriage;
    }

    private ArrayList<ArrayList<Integer>> sortValidPartners(Matching data,int validPartnersNo, ArrayList<ArrayList<Integer>> validPartners)
    {
        /*sorting each resident's valid partners using insertion sort*/
        for(int resident=0;resident<validPartners.get(0).size();resident++){

            for(int partner=1; partner<validPartnersNo;partner++) {
                int partnerRank = data.getResidentPreference().get(resident).indexOf(validPartners.get(partner).get(resident));
                int partnerValue=validPartners.get(partner).get(resident);
                if(partnerRank==-1)
                    partnerRank=999999;
                int partnerToCompare = partner - 1;

                int partnerToCompareRank = data.getResidentPreference().get(resident).indexOf(validPartners.get(partnerToCompare).get(resident));
                if(partnerToCompareRank==-1)
                    partnerToCompareRank=999999;
                while ( partnerToCompareRank > partnerRank) {
                    validPartners.get(partnerToCompare + 1).set(resident, validPartners.get(partnerToCompare).get(resident));
                    partnerToCompare--;
                    if(partnerToCompare<0)
                        break;
                     partnerToCompareRank = data.getResidentPreference().get(resident).indexOf(validPartners.get(partnerToCompare).get(resident));
                    if(partnerToCompareRank==-1)
                        partnerToCompareRank=999999;
                }

                validPartners.get(partnerToCompare + 1).set(resident, partnerValue);
            }
        }
        return validPartners;
    }

    /*perfect matching will simply be the one with the first Arraylist*/
    private Matching getBruteForceCorrectMatching(ArrayList<Matching> stableMatchings,ArrayList<ArrayList<Integer>> validPartners){
        for(int matching=0; matching<stableMatchings.size(); matching++) {
            if (stableMatchings.get(matching).getResidentMatching().equals(validPartners.get(0)))
                return stableMatchings.get(matching);
        }
        return null;
    }

    private ArrayList<Integer> arrlistInit(ArrayList<Integer> arrlist, int n,int value, boolean proposing){
        for(int i=0;i<n;i++) {
            if (proposing)
                arrlist.add(i);
            else
                arrlist.add(value);
        }
        return arrlist;
    }

}
