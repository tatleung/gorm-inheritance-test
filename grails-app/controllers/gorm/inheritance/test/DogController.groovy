package gorm.inheritance.test

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class DogController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Dog.list(params), model:[dogCount: Dog.count()]
    }

    def show(Dog dog) {
        respond dog
    }

    def create() {
        respond new Dog(params)
    }

    @Transactional
    def save(Dog dog) {
        if (dog == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (dog.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond dog.errors, view:'create'
            return
        }

        dog.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'dog.label', default: 'Dog'), dog.id])
                redirect dog
            }
            '*' { respond dog, [status: CREATED] }
        }
    }

    def edit(Dog dog) {
        respond dog
    }

    @Transactional
    def update(Dog dog) {
        if (dog == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (dog.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond dog.errors, view:'edit'
            return
        }

        dog.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'dog.label', default: 'Dog'), dog.id])
                redirect dog
            }
            '*'{ respond dog, [status: OK] }
        }
    }

    @Transactional
    def delete(Dog dog) {

        if (dog == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        dog.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'dog.label', default: 'Dog'), dog.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'dog.label', default: 'Dog'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
