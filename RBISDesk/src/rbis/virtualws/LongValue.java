package rbis.virtualws;

public class LongValue implements DataValue {

    private Long value;

    public LongValue(long value) {
        this.value = new Long(value);
    }

    public double getDouble() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getLong() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getObject() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setDouble(double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setLong(long value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setString(String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setObject(Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

