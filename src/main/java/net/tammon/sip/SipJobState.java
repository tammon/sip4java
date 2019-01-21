package net.tammon.sip;

import net.tammon.sip.packets.Data;

/**
 * The Interface SipJobState.
 */
public interface SipJobState {
    
    /**
     * Gets the job.
     *
     * @return the job
     */
    SipJob getJob();
    
    /**
     * Gets the data.
     *
     * @return the data
     */
    Data getData();
    
    /**
     * Gets the exception.
     *
     * @return the exception
     */
    Exception getException();
}
