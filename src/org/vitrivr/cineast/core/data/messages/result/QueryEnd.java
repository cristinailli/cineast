package org.vitrivr.cineast.core.data.messages.result;

import org.vitrivr.cineast.core.data.messages.interfaces.Message;
import org.vitrivr.cineast.core.data.messages.interfaces.MessageTypes;

/**
 * @author rgasser
 * @version 1.0
 * @created 12.01.17
 */
public class QueryEnd implements Message {
    /**
     *
     */
    private final String queryId;

    /**
     *
     */
    public QueryEnd(String queryId) {
        this.queryId = queryId;
    }

    /**
     *
     * @return
     */
    @Override
    public MessageTypes getMessagetype() {
        return MessageTypes.QR_END;
    }

    /**
     *
     * @return
     */
    public String getQueryId() {
        return queryId;
    }
}
