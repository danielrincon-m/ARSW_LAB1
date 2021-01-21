/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int N) {
        LinkedList<Integer> blackListOcurrences = new LinkedList<>();
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        ArrayList<ThreadValidator> validators = new ArrayList<>();

        int ocurrencesCount = 0;
        int checkedListsCount = 0;
        int serverNumber = skds.getRegisteredServersCount();

        for (int i = 0; i < N; i++){
            int lowerLimit = serverNumber / N * i;
            int upperLimit = i == N - 1 ? serverNumber - 1 : serverNumber / N * (i + 1) - 1;
            ThreadValidator validator = new ThreadValidator(lowerLimit, upperLimit, ipaddress);
            validators.add(validator);
            validator.start();
        }
        for (ThreadValidator validator : validators) {
            try {
                validator.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (ThreadValidator validator : validators) {
            blackListOcurrences.addAll(validator.getOcurrences());
            checkedListsCount += validator.getCheckedListsCount();
        }
        ocurrencesCount = blackListOcurrences.size();

        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    
    
}
