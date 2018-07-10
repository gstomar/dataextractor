package com.joity.property.data.pojo;

public class PropertyData {

    private final String postCode;
    private final String houseType;
    private final long housePrice;
    private final short rentalIncome;
    private final float rentalReturn;

    public PropertyData(String postCode, String houseType, long housePrice, short rentalIncome) {
        this.postCode = postCode;
        this.houseType = houseType;
        this.housePrice = housePrice;
        this.rentalIncome = rentalIncome;
        this.rentalReturn = calculateRentalReturn(housePrice, rentalIncome);
    }

    private float calculateRentalReturn(long housePrice, short rentalIncome) {
        if((rentalIncome == 0) || (housePrice == 0)) {
            return 0;
        }
        return (rentalIncome * 12 * 100)/housePrice;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getHouseType() {
        return houseType;
    }

    public long getHousePrice() {
        return housePrice;
    }

    public short getRentalIncome() {
        return rentalIncome;
    }

    public float getRentalReturn() {
        return rentalReturn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyData)) return false;

        PropertyData that = (PropertyData) o;

        if (housePrice != that.housePrice) return false;
        if (rentalIncome != that.rentalIncome) return false;
        if (postCode != null ? !postCode.equals(that.postCode) : that.postCode != null) return false;
        return houseType != null ? houseType.equals(that.houseType) : that.houseType == null;
    }

    @Override
    public int hashCode() {
        int result = postCode != null ? postCode.hashCode() : 0;
        result = 31 * result + (houseType != null ? houseType.hashCode() : 0);
        result = 31 * result + (int) (housePrice ^ (housePrice >>> 32));
        result = 31 * result + (int) rentalIncome;
        return result;
    }

    public String getCsvString(String delimeter) {
        return new StringBuilder()
            .append(postCode)
            .append(delimeter)
            .append(houseType)
            .append(delimeter)
            .append(housePrice)
            .append(delimeter)
            .append(rentalIncome)
            .append(delimeter)
            .append(rentalReturn)
            .toString();
    }

    public static final class PropertyDataBuilder {
        private String postCode;
        private String houseType;
        private long housePrice;
        private short rentalIncome;

        private PropertyDataBuilder() {
        }

        public static PropertyDataBuilder builder() {
            return new PropertyDataBuilder();
        }

        public PropertyDataBuilder withPostCode(String postCode) {
            this.postCode = postCode;
            return this;
        }

        public PropertyDataBuilder withHouseType(String houseType) {
            this.houseType = houseType;
            return this;
        }

        public PropertyDataBuilder withHousePrice(long housePrice) {
            this.housePrice = housePrice;
            return this;
        }

        public PropertyDataBuilder withRentalIncome(short rentalIncome) {
            this.rentalIncome = rentalIncome;
            return this;
        }

        public PropertyData build() {
            return new PropertyData(postCode, houseType, housePrice, rentalIncome);
        }
    }
}
