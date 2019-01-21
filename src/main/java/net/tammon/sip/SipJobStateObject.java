package net.tammon.sip;

import net.tammon.sip.packets.Data;

/**
 * Helper class SipJobStateObject.
 */
final public class SipJobStateObject implements SipJobState {
    
    /** The request. */
    private SipJob _request;
    
    /** The data. */
    private Data _data;
    
    /** The exception. */
    private Exception _exception;

    /* (non-Javadoc)
     * @see net.tammon.sip.SipJobState#getJob()
     */
    @Override
    public SipJob getJob() {
        return _request;
    }

    /* (non-Javadoc)
     * @see net.tammon.sip.SipJobState#getData()
     */
    @Override
    public Data getData() {
        return _data;
    }

    /* (non-Javadoc)
     * @see net.tammon.sip.SipJobState#getException()
     */
    @Override
    public Exception getException() {
        return _exception;
    }
    
    /**
     * Sets the request.
     *
     * @param request the new request
     */
    public void setRequest(SipJob request) {
        this._request = request;        
    }

    /**
     * Sets the data.
     *
     * @param data the new data
     */
    public void setData(Data data) {
        this._data = data;
    }

    /**
     * Sets the exception.
     *
     * @param exception the new exception
     */
    public void setException(Exception exception) {
        this._exception = exception;
    }
}
