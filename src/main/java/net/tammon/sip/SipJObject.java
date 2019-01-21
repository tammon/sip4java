package net.tammon.sip;

/**
 * Helper Calls for SipJob
 */
final public class SipJObject implements SipJob {
    
    int _slaveIndex;
    int _slaveExtension;
    String _Idn;

    /**
     * Instantiates a new sip J object.
     *
     * @param slaveindex the slaveindex
     * @param slaveextension the slaveextension
     * @param idn the idn
     */
    public SipJObject(int slaveindex, int slaveextension, String idn) {
        _slaveIndex =  slaveindex;
        _slaveExtension = slaveextension;
        _Idn = idn;
    }
    
    /* (non-Javadoc)
     * @see net.tammon.sip.SipJob#getSlaveIndex()
     */
    @Override
    public int getSlaveIndex() {
        return _slaveIndex;
    }

    /* (non-Javadoc)
     * @see net.tammon.sip.SipJob#getSlaveExtension()
     */
    @Override
    public int getSlaveExtension() {
        return _slaveExtension;
    }

    /* (non-Javadoc)
     * @see net.tammon.sip.SipJob#getIdn()
     */
    @Override
    public String getIdn() {
        return _Idn;
    }
    
}