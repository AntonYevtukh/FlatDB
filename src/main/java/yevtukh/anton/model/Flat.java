package yevtukh.anton.model;

import javax.persistence.*;

/**
 * Created by Anton on 14.10.2017.
 */
@Entity
@Table(name = "Flats")
@NamedQuery(name = "Flat.selectAll", query = "SELECT f FROM Flat f")
public class Flat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "district", nullable = false)
    @Enumerated
    private District district;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "rooms", nullable = false)
    private int rooms;

    @Column(name = "area", nullable = false)
    private int area;

    @Column(name = "price", nullable = false)
    private int price;

    public Flat() {
        this.district = District.values()[0];
        this.address = "";
        this.rooms = 1;
        this.area = 0;
        this.price = 0;
    }

    public Flat(District district, String address, int rooms, int area, int price) {
        this.district = district;
        this.address = address;
        this.rooms = rooms;
        this.area = area;
        this.price = price;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Flat{" +
                "district=" + district +
                ", address='" + address + '\'' +
                ", rooms=" + rooms +
                ", area=" + area +
                ", price=" + price +
                '}';
    }
}
