package co.kaizenpro.mainapp.mainapp;




    public class ItemReview {
    private int id_review;
    private String nombre;
    private String info;
    private String imagenId;
    private Float rating;

        public ItemReview(int id_review, String nombre, String info, String imagenId, Float rating) {
            this.id_review = id_review;
            this.nombre = nombre;
            this.info = info;
            this.imagenId = imagenId;
            this.rating = rating;
        }

    public int getIdReview() {
        return id_review;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImagenId() {
        return imagenId;
    }

    public void setImagenId(String imagenId) {
        this.imagenId = imagenId;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}
