package com.gsa.gc.swisstrip;

/**
 * @author gianc
 * @version 12.11.21
 *
 * City Klasse
 */
public class City {
    private int id;
    private String name;
    private double xPos;
    private double yPos;

    public City(int id, String name, double xPos, double yPos) {
        this.id = id;
        this.name = name;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getxPos() {
        return xPos;
    }

    public void setxPos(double xPos) {
        this.xPos = xPos;
    }

    public double getyPos() {
        return yPos;
    }

    public void setyPos(double yPos) {
        this.yPos = yPos;
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", xPos=" + xPos +
                ", yPos=" + yPos +
                '}';
    }
}
