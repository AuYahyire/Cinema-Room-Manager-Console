package cinema

import java.lang.NumberFormatException

const val DEFAULT_PRICE = 10
const val DISCOUNT_PRICE = 8
const val SEAT_CHAR = 'S'
const val SELECTED_SEAT_CHAR = 'B'
const val MAX_SEATS_WITHOUT_DISCOUNT = 60
const val INVALID_INPUT = "Invalid input!"


/**@authors HyperSkill: AugelloG, GitHub: AuYahyire*/

fun main() {
    //Build the cinema room
    val cinemaRoomState = CinemaRoomData.build

    //Show menu
    do {
        cinemaRoomMainMenu(cinemaRoomState)
    } while (cinemaRoomState.turnOnCinemaManager)


}

/**
 * Prints the MainMenu and calls the correspondent function.*/

fun cinemaRoomMainMenu(cinemaRoomData: CinemaRoomData) {
    println("1. Show the seats")
    println("2. Buy a ticket")
    println("3. Statistics")
    println("0. Exit")
    println()
    return when (readln().toIntOrNull()) {
        1 -> showCinemaRoom(cinemaRoomData) //Prints the cinemaRoom
        2 -> { //Steps to perform the buy.
            selectSeat(cinemaRoomData)
            addSeatToCount(cinemaRoomData)
            updateSeatStatus(cinemaRoomData)
            showPriceOfTicket(cinemaRoomData)
            updatePercentage(cinemaRoomData)
        }

        3 -> statistics(cinemaRoomData)
        0 -> cinemaRoomData.turnOnCinemaManager = false // Returns OFF signal to main function through cinemaRoomData
        else -> println("Not a valid option.\n")
    }
}


/**
 * Reads the user inputs and creates the cinemaRoom with a 2D mutable list.
 */
fun buildCinemaRoom(): MutableList<MutableList<Char>> {
    try {
        println("Enter the number of rows:")
        val numberOfRows = readln().toInt()
        println("Enter the number of seats in each row:")
        val numberOfSeats = readln().toInt()
        println()

        val cinemaRoom = mutableListOf<MutableList<Char>>()

        //Checks if values is more than 0.
        if (numberOfRows > 0 && numberOfSeats > 0) {
            //Create cinema rows
            for (i in 0 until numberOfRows) {
                cinemaRoom.add(mutableListOf())
                //Adding seats to each row.
                for (j in 0 until numberOfSeats) {
                    cinemaRoom[i].add(SEAT_CHAR)
                }
            }

            return cinemaRoom
        } else {
            println(INVALID_INPUT)
            println()
        }

    } catch (e: NumberFormatException) {
        println(INVALID_INPUT)
        println()
    }

    return buildCinemaRoom()
}

/**
 * Displays the cinema room*/
fun showCinemaRoom(cinemaRoomData: CinemaRoomData) {
    println("Cinema:")
    print("  ")
    for (columnNumber in 1..cinemaRoomData.cinemaRoom[0].size) {
        print("$columnNumber ")
    }
    println()

    for ((rowIndex, row) in cinemaRoomData.cinemaRoom.withIndex()) {
        print("${rowIndex + 1} ")
        for (seat in row) {
            print("$seat ")
        }
        println()
    }
    println()
}

/**Reads the user desired seat*/
fun selectSeat(cinemaRoomData: CinemaRoomData) {
    var validData = false

    do {
        println("Enter a row number:")
        val selectedRow = readln().toIntOrNull()?.minus(1) // Minus 1 to match indexes
        println("Enter a seat number in that row:")
        val selectedSeat = readln().toIntOrNull()?.minus(1)
        println()

        when {
            //If null...
            selectedRow == null || selectedSeat == null -> {
                println(INVALID_INPUT)
                println()
            }

            //If isn't a valid range...
            !isAValidRange(cinemaRoomData, selectedRow, selectedSeat) -> {
                println("Wrong input!")
                println()
            }

            //If seat is already sold...
            isSeatUnavailable(cinemaRoomData, selectedRow, selectedSeat) -> {
                println("That ticket has already been purchased!")
                println()
            }

            else -> {
                cinemaRoomData.selectedRow = selectedRow
                cinemaRoomData.selectedSeat = selectedSeat
                validData = true //close the loop.
            }
        }
        println() // Add an empty line for readability

    } while (!validData)
}

/**Checks if selected row and seat is inside the size of the data in the list.*/
private fun isAValidRange(cinemaRoomData: CinemaRoomData, row: Int, seat: Int): Boolean {
    // minus 1 to match indexes, because .size returns from 1 to max size.
    val numberOfRows = cinemaRoomData.cinemaRoom.size.minus(1)
    val numberOfSeats = cinemaRoomData.cinemaRoom[0].size.minus(1)
    return row in 0..numberOfRows && seat in 0..numberOfSeats
}


/**Checks if selected seat is already marked as 'B'*/
private fun isSeatUnavailable(cinemaRoomData: CinemaRoomData, row: Int, seat: Int): Boolean {
    return cinemaRoomData.cinemaRoom[row][seat] == SELECTED_SEAT_CHAR
}

/**Updates the number of purchased seats*/
fun addSeatToCount(cinemaRoomData: CinemaRoomData) {
    cinemaRoomData.numberOfPurchasedTickets += 1
}

/**Updates the cinema room mutable list with the purchased seat*/
fun updateSeatStatus(cinemaRoomData: CinemaRoomData) {
    val selectedRow = cinemaRoomData.selectedRow
    val selectedSeat = cinemaRoomData.selectedSeat

    cinemaRoomData.cinemaRoom[selectedRow][selectedSeat] = SELECTED_SEAT_CHAR
}

/**Shows the price of ticket to user*/
fun showPriceOfTicket(cinemaRoomData: CinemaRoomData) {
    val totalRows = cinemaRoomData.cinemaRoom.size
    val seatsPerRow = cinemaRoomData.cinemaRoom[0].size
    val selectedRow = cinemaRoomData.selectedRow + 1
    val totalSeats = totalRows * seatsPerRow


    when {
        totalSeats <= MAX_SEATS_WITHOUT_DISCOUNT -> {
            println("Ticket price: $${DEFAULT_PRICE}")
            println()
            cinemaRoomData.currentIncome += DEFAULT_PRICE //Updates the current income
        }

        else -> {
            val ticketPrice = ticketPriceForBigRoom(totalRows, selectedRow) //Calls the logic for bigger rooms
            println("Ticket price: $$ticketPrice")
            println()
            updateCurrentIncome(cinemaRoomData, ticketPrice) //Updates the current income
        }
    }
}

/**Logic to calculate ticket price for big rooms
 * @return the price of the ticket*/
private fun ticketPriceForBigRoom(totalRows: Int, selectedRow: Int): Int {
    val numberOfDefaultRows = totalRows / 2
    return if (selectedRow <= numberOfDefaultRows) DEFAULT_PRICE else DISCOUNT_PRICE
}

/**Updates the current income*/
private fun updateCurrentIncome(cinemaRoomData: CinemaRoomData, ticketPrice: Int) {
    cinemaRoomData.currentIncome += ticketPrice
}

/**Prints a list of statistics*/
fun statistics(cinemaRoomData: CinemaRoomData) {
    val formatPercentage = "%.2f".format(cinemaRoomData.percentage)

    println("Number of purchased tickets: ${cinemaRoomData.numberOfPurchasedTickets}")
    println("Percentage: ${formatPercentage}%")
    println("Current income: $${cinemaRoomData.currentIncome}")
    println("Total income: $${totalIncome(cinemaRoomData)}")
    println()
}

fun updatePercentage(cinemaRoomData: CinemaRoomData) {
    val numberOfPurchasedTickets = cinemaRoomData.numberOfPurchasedTickets
    val totalRows = cinemaRoomData.cinemaRoom.size
    val seatsPerRow = cinemaRoomData.cinemaRoom[0].size
    val totalSeats = (totalRows * seatsPerRow).toDouble()

    cinemaRoomData.percentage = (numberOfPurchasedTickets / totalSeats) * 100

}

/**Perform the logic to calculate the total income per tickets' price based on the size of the cinemaRoom */
fun totalIncome(cinemaRoomData: CinemaRoomData): Int {
    val totalSeats = cinemaRoomData.cinemaRoom.size * cinemaRoomData.cinemaRoom[0].size
    val pricePerSeat = DEFAULT_PRICE


    return if (totalSeats <= MAX_SEATS_WITHOUT_DISCOUNT) {
        pricePerSeat * totalSeats
    } else {
        incomeForBigRoom(cinemaRoomData.cinemaRoom.size, cinemaRoomData.cinemaRoom[0].size)
    }
}


fun incomeForBigRoom(rows: Int, seats: Int): Int {
    val numberOfVipRows: Int
    val numberOfNormalRows: Int

    val priceVipSeats = 10
    val priceNormalSeats = 8

    if (rows % 2 == 1) {
        numberOfVipRows = rows / 2
        numberOfNormalRows = numberOfVipRows + 1
    } else {
        numberOfVipRows = rows / 2
        numberOfNormalRows = numberOfVipRows
    }

    return ((numberOfVipRows * seats) * priceVipSeats) + ((numberOfNormalRows * seats) * priceNormalSeats)
}

/**Data class with all the shared values across functions*
 * @param turnOnCinemaManager Switch to turn on/off the program.
 * @param cinemaRoom stores the cinema room data.
 * @param selectedRow row selected by user when buying a ticket, the same for [selectedSeat]
 * @param numberOfPurchasedTickets total amount of purchased tickets so far
 * @param percentage of tickets bought from total available.
 */
data class CinemaRoomData(
    var turnOnCinemaManager: Boolean,
    val cinemaRoom: MutableList<MutableList<Char>>,
    var selectedRow: Int,
    var selectedSeat: Int,
    var numberOfPurchasedTickets: Int,
    var percentage: Double,
    var currentIncome: Int
) {
    /** Initializer to gather the data from user through [buildCinemaRoom] about the size of the cinema Room. */
    companion object {
        val build = CinemaRoomData(
            turnOnCinemaManager = true,
            cinemaRoom = buildCinemaRoom(),
            selectedRow = 0,
            selectedSeat = 0,
            numberOfPurchasedTickets = 0,
            percentage = 0.00,
            currentIncome = 0
        )
    }
}
