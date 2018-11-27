package co.kaizenpro.mainapp.mainapp;




    public class ItemServicio {
    private int id_servicio;
    private String nombre;
    private String info;
    private String precio;

        public ItemServicio(int id_servicio, String nombre, String info, String precio ) {
            this.id_servicio = id_servicio;
            this.nombre = nombre;
            this.info = info;
            this.precio = precio;
        }
    public int getIdServicio() {
        return id_servicio;
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

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    /*public int getImagenId() {
        return imagenId;
    }

    public void setImagenId(int imagenId) {
        this.imagenId = imagenId;
    }
*/
}
