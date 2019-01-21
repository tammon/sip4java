package net.tammon.sip;

/**
 * The Interface SipJob.
 */
public interface SipJob {
    
    /**
     * Gets the slave index.
     *
     * @return the slave index
     */
    int getSlaveIndex();
    
    /**
     * Gets the slave extension.
     *
     * @return the slave extension
     */
    int getSlaveExtension();
    
    /**
     * Gets the idn.
     *
     * @return the idn
     */
    String getIdn();
}