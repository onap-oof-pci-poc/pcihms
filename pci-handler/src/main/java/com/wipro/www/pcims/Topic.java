package com.wipro.www.pcims;

public class Topic {

    private String name;
    private String producer;
    private String consumer;

    public Topic() {

    }

    /**
     * Parameterized constructor.
     */
    public Topic(String name, String producer, String consumer) {
        super();
        this.name = name;
        this.producer = producer;
        this.consumer = consumer;
    }

    @Override
    public String toString() {
        return "topic [name=" + name + ", producer=" + producer + ", consumer=" + consumer + "]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

}
