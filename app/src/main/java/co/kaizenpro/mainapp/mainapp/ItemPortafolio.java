package co.kaizenpro.mainapp.mainapp;




    public class ItemPortafolio {

    private int id_item;
    private String nombre;
    private String info;
    private String imagenId;

    public ItemPortafolio(int id_item, String nombre, String info, String imagenId) {
        this.id_item = id_item;
            this.nombre = nombre;
            this.info = info;
            this.imagenId = imagenId;
        }
    public int getIdItem() {
        return id_item;
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
}
