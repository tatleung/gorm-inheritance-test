package gorm.inheritance.test

import grails.mongodb.MongoEntity

class Pet extends Animal implements MongoEntity<Pet> {

    String name

    static constraints = {
    }
}
