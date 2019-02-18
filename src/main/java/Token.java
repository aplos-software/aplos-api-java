public class Token {
    private String version;
    private Integer status;
    private Data data;

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        private String expires;
        private String token;

        /**
         * @return the expires
         */
        public String getExpires() {
            return expires;
        }

        /**
         * @return the token
         */
        public String getToken() {
            return token;
        }
        
        /**
         * @param token the token to set
         */
        public void setToken(String token) {
            this.token = token;
        }

        /**
         * @param expires the expires to set
         */
        public void setExpires(String expires) {
            this.expires = expires;
        }
    }

}