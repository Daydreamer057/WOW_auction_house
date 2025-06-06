# 🧙‍♂️ WoW Cross-Realm Trading Price Scraper

A Java-based Spring Boot tool to fetch and update prices of **mounts**, **battle pets**, and **recipes** from **all connected realms** in *World of Warcraft* (EU region), enabling players and traders to identify lucrative cross-realm trading opportunities.

---

## 📌 Features

- 🔄 Fetches real-time auction house data via Blizzard's [WoW API](https://develop.battle.net/documentation/world-of-warcraft)
- 💰 Tracks prices of high-value items (mounts, battle pets, and recipes)
- 🌍 Scans all connected realms in the EU region
- 📦 Saves item price data to a database for further analysis or display
- 🧵 Multi-threaded fetching for speed and efficiency

---

## ⚙️ Tech Stack

- Java 17+
- Spring Boot
- Hibernate / JPA
- MySQL or any SQL-compatible database
- Jackson (for JSON parsing)
- Apache HttpClient (for API requests)

---

## 🚀 Getting Started

### 🔐 Prerequisites

- Blizzard Developer API credentials
- Java 17+
- Maven or Gradle
- PostgreSQL/MySQL (or your preferred DB)

### 🔧 Configuration

Set your API credentials in `application.properties` or as environment variables:

```properties
CLIENT_ID=your_blizzard_client_id
CLIENT_SECRET=your_blizzard_client_secret

---

## 🛡️ Legal

World of Warcraft and Blizzard Entertainment are trademarks or registered trademarks of Blizzard Entertainment, Inc. in the U.S. and/or other countries.  
This project is not affiliated with, endorsed by, or in any way officially connected with Blizzard Entertainment.  
All game content, imagery, and data are the property of Blizzard Entertainment.

© Blizzard Entertainment, Inc. All rights reserved.
