#  TechRent - IT Equipment Rental Management

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-blue?style=for-the-badge)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

**TechRent Pro** is a comprehensive desktop application designed to manage the lifecycle of IT equipment rentals (PCs, projectors, servers, etc.). It allows for the management of inventory, clients, rental contracts, and maintenance tracking via a modern and intuitive interface.

---

##  Overview

| Dashboard | Rental Planning |
|:---:|:---:|
| ![Dashboard](image_fbef28.png) | ![Planning](image_fb8672.png) |
| *Real-time statistics and alerts* | *Interactive calendar view (CalendarFX)* |

---

##  Key Features

### 1. Equipment Management 
* Complete inventory with product photos.
* Real-time status tracking: `AVAILABLE`, `RENTED`, `OUT OF ORDER`, `UNDER MAINTENANCE`.
* Dedicated maintenance module to manage repairs and disposals.

### 2. Rental Management 
* **Anti-Conflict System:** Automatic verification of date overlaps to prevent double bookings.
* **Visual Calendar:** Global view of past, current, and future rentals.
* **Invoicing:** Automatic calculation of total cost and management of late fees (Rate x 2).

### 3. Client Management 
* Complete client database.
* Rental history per client.

### 4. Reports & Exports 
* Dashboard with KPIs (Estimated revenue, Delays, Occupancy rate).
* PDF generation for contracts or inventory lists.

---

## 🛠️ Technical Architecture

The project scrupulously respects the **MVC / Service / DAO** layered architecture to ensure easy maintenance and separation of concerns.



[Image of Layered Software Architecture]


* **View (Presentation):** FXML + JavaFX Controller. Modern interface with the *AtlantaFX (Cupertino Dark)* theme.
* **Service (Business Logic):** Contains all logic (price calculations, availability checks, business rules).
* **DAO (Data Access):** Transaction management with the database via **Hibernate** (ORM).
* **Model:** JPA entities mapped to the database.

---

##  Installation & Startup

### Prerequisites
* **Java JDK 21** (or higher).
* **MySQL** or **SQL Server** (WAMP/XAMPP recommended for MySQL).
* **Maven** (for dependency management).

### Installation Steps

1.  **Clone the project:**
    ```bash
    git clone [https://github.com/YOUR_USERNAME/TechRent.git](https://github.com/YOUR_USERNAME/TechRent.git)
    cd TechRent
    ```

2.  **Database Configuration:**
    * Create an empty database named `TechRentDB`.
    * If you have the provided SQL script (`database_setup.sql`), execute it. Otherwise, Hibernate will create the tables upon first launch.
    * Modify the `src/main/resources/hibernate.cfg.xml` file with your credentials:
        ```xml
        <property name="connection.username">root</property>
        <property name="connection.password">your_password</property>
        ```

3.  **Launch the application:**
    * Via IntelliJ / Eclipse: Run the `com.techrent.App` class.
    * **Default Login:**
        * User: `admin`
        * Password: `admin123`

---

##  Libraries Used

* **JavaFX:** GUI Framework.
* **Hibernate ORM:** Data persistence management.
* **CalendarFX:** Professional calendar component.
* **AtlantaFX:** Modern themes for JavaFX.
* **iText / PDFBox:** PDF report generation.
* **Lombok:** Boilerplate code reduction (Getters/Setters).

---
