package com.wipro.www.pcims.model;

public class CellNeighbourList {

    private String cellId;
    private int physicalCellId;
    private String neighbours;

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public int getPhysicalCellId() {
        return physicalCellId;
    }

    public void setPhysicalCellId(int physicalCellId) {
        this.physicalCellId = physicalCellId;
    }

    public String getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(String neighbours) {
        this.neighbours = neighbours;
    }

    /**
     * constructor.
     */
    public CellNeighbourList() {

    }

    /**
     * Parameterized constructor.
     */
    public CellNeighbourList(String cellId, int physicalCellId, String neighbours) {
        super();
        this.cellId = cellId;
        this.physicalCellId = physicalCellId;
        this.neighbours = neighbours;
    }
}
