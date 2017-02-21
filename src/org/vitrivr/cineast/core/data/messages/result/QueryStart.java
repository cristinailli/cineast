package org.vitrivr.cineast.core.data.messages.result;

import org.vitrivr.cineast.core.data.messages.interfaces.Message;
import org.vitrivr.cineast.core.data.messages.interfaces.MessageTypes;

import java.util.UUID;

/**
 * @author rgasser
 * @version 1.0
 * @created 12.01.17
 */
public class QueryStart implements Message {
    /**
     * Unique ID of the QueryStart message. This ID establishes a context which is importent
     * for all further communication.
     */
    private final String queryId;

    /**
     * Default constructor; generates the QueryId as random UUID.
     */
    public QueryStart() {
        this.queryId = UUID.randomUUID().toString();
    }

    /**
     *
     * @return
     */
    @Override
    public MessageTypes getMessagetype() {
        return MessageTypes.QR_START;
    }

    /**
     *
     * @return
     */
    public String getQueryId() {
        return queryId;
    }
}
