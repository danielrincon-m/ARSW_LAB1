package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.LinkedList;

/**
 * Authors: Laura Bernal, Paula Guevara, Daniel Rincón
 */
public class ThreadValidator extends Thread {
    private final int lowerLimit;
    private final int upperLimit;
    private int checkedListsCount;

    private final LinkedList<Integer> blackListOcurrences;

    private final String ipaddress;

    /**
     * Check the given host's IP address in the black lists between Lower Limit and Upper Limit, and counts the
     * number of occurrences of the IP address in the lists.
     * @param lowerLimit Lower bound of the black list collection to check
     * @param upperLimit Upper bound of the black list collection to check
     * @param ipaddress IP address to look for on the black lists
     */
    public ThreadValidator(int lowerLimit, int upperLimit, String ipaddress) {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.ipaddress = ipaddress;
        blackListOcurrences = new LinkedList<>();
    }

    @Override
    public void run() {
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        checkedListsCount = 0;

        for (int i=lowerLimit; i <= upperLimit; i++){
            checkedListsCount++;
            if (skds.isInBlackListServer(i, ipaddress)){
                blackListOcurrences.add(i);
            }
        }
    }

    /**
     *
     * @return The number of occurrences of the IP address in the black lists
     */
    public LinkedList<Integer> getOcurrences() {
        return blackListOcurrences;
    }

    /**
     * @return The number of checked black lists
     */
    public int getCheckedListsCount() {
        return  checkedListsCount;
    }
}
