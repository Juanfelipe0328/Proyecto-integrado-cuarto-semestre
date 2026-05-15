package com.gestionatusalon.modelo;

public class RequerimientoLogistico {

    private int idReq;
    private int idReserva;
    private int sillasAdicionales;
    private float configuracionAC;
    private boolean requiereApoyoTecnico;

    public RequerimientoLogistico() {
    }

    public RequerimientoLogistico(int idReq, int idReserva, int sillasAdicionales,
                                  float configuracionAC, boolean requiereApoyoTecnico) {
        this.idReq = idReq;
        this.idReserva = idReserva;
        this.sillasAdicionales = sillasAdicionales;
        this.configuracionAC = configuracionAC;
        this.requiereApoyoTecnico = requiereApoyoTecnico;
    }

    public OrdenServicio generarOrdenServicio() {
        OrdenServicio orden = new OrdenServicio();
        orden.setIdOrden(0);
        orden.setIdAuxiliar(0);
        orden.setEstadoEntrega("PENDIENTE");
        return orden;
    }

    public double estimarCostoAdicional() {
        double costo = 0;

        costo += sillasAdicionales * 2000;

        if (requiereApoyoTecnico) {
            costo += 15000;
        }

        return costo;
    }

    public int getIdReq() {
        return idReq;
    }

    public void setIdReq(int idReq) {
        this.idReq = idReq;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public int getSillasAdicionales() {
        return sillasAdicionales;
    }

    public void setSillasAdicionales(int sillasAdicionales) {
        this.sillasAdicionales = sillasAdicionales;
    }

    public float getConfiguracionAC() {
        return configuracionAC;
    }

    public void setConfiguracionAC(float configuracionAC) {
        this.configuracionAC = configuracionAC;
    }

    public boolean isRequiereApoyoTecnico() {
        return requiereApoyoTecnico;
    }

    public void setRequiereApoyoTecnico(boolean requiereApoyoTecnico) {
        this.requiereApoyoTecnico = requiereApoyoTecnico;
    }

    @Override
    public String toString() {
        return "RequerimientoLogistico{" +
                "idReq=" + idReq +
                ", idReserva=" + idReserva +
                ", sillasAdicionales=" + sillasAdicionales +
                ", configuracionAC=" + configuracionAC +
                ", requiereApoyoTecnico=" + requiereApoyoTecnico +
                '}';
    }
}