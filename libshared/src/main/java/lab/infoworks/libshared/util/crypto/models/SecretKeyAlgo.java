package lab.infoworks.libshared.util.crypto.models;

public enum SecretKeyAlgo {

    AES("AES",256),
    DES("DES", 56),
    DESede("DESede", 112),
    TripleDES("TripleDES", 168),
    RSA("RSA", 128);

    private String description;
    private int length;

    SecretKeyAlgo(String description, int length) {
        this.description = description;
        this.length = length;
    }

    @Override
    public String toString() {
        return "SecretKeyAlgo{" +
                "description='" + description + '\'' +
                ", length=" + length +
                '}';
    }

    public int length(){
        return length;
    }
}
