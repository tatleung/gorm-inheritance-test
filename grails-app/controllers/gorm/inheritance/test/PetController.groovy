package gorm.inheritance.test

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class PetController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Pet.list(params), model:[petCount: Pet.count()]
    }

    def show(Pet pet) {
        respond pet
    }

    def create() {
        respond new Pet(params)
    }

    @Transactional
    def save(Pet pet) {
        if (pet == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (pet.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond pet.errors, view:'create'
            return
        }

        pet.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'pet.label', default: 'Pet'), pet.id])
                redirect pet
            }
            '*' { respond pet, [status: CREATED] }
        }
    }

    def edit(Pet pet) {
        respond pet
    }

    @Transactional
    def update(Pet pet) {
        if (pet == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (pet.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond pet.errors, view:'edit'
            return
        }

        pet.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'pet.label', default: 'Pet'), pet.id])
                redirect pet
            }
            '*'{ respond pet, [status: OK] }
        }
    }

    @Transactional
    def delete(Pet pet) {

        if (pet == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        pet.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'pet.label', default: 'Pet'), pet.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'pet.label', default: 'Pet'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
