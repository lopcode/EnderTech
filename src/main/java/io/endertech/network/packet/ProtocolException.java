package io.endertech.network.packet;

public class ProtocolException extends Exception
{
    public ProtocolException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ProtocolException(String message)
    {
        super(message);
    }

    public ProtocolException(Throwable cause)
    {
        super(cause);
    }
}