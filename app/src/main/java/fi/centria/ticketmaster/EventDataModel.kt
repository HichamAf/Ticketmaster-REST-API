package fi.centria.ticketmaster

class EventDataModel {
    var name: String = ""
    var date: String = ""
    var time: String = ""
    var type: String = ""
    var img: String = ""
    var city: String = ""
    var venue: String =""
    var address: String = ""
    var link: String = ""
    var id: String = ""

    constructor(
        name: String,
        date: String,
        time: String,
        type: String,
        img: String,
        city: String,
        venue: String,
        address: String,
        link: String,
        id: String
    ) {
        this.name = name
        this.date = date
        this.time = time
        this.type = type
        this.img = img
        this.city = city
        this.venue = venue
        this.address = address
        this.link = link
        this.id = id
    }
}