# 🚀 NetAnalyzer – Smart Network Scanner

NetAnalyzer is a lightweight Java-based network scanning tool with a web-based frontend.  
It scans a specified subnet range and lists **online** and **offline** hosts along with hostname and latency information.

---

## 📌 Features
- ✅ Scans a given subnet within a specified IP range  
- ✅ Detects devices that are online/offline  
- ✅ Displays **hostname** and **latency** for each device  
- ✅ Easy-to-use **web interface (index.html)**  
- ✅ Download scan results as a `.csv` file  

---

## 🖼️ Screenshot
![NetAnalyzer UI](https://github.com/NeelakshiSolanki/Network_Scanner/blob/5bd69fd52030fd40e06e11b587fe717df017affc/Screenshot%202025-08-04%20154020.png)

---

## ⚙️ Requirements
- **Java 8 or higher**  
- **Web browser** (for frontend UI)  

---
## 🔍 How to Find Your Subnet

Before running the scanner, you need your subnet (e.g., `172.16.37` or `192.168.1`).  
Follow these steps:

### **Windows**
1. Press `Win + R`, type `cmd`, and press Enter.  
2. Run:
   ```cmd
   ipconfig 
---------------------------------------------------------------------------------------------------------------------------------------------------------------------

## ▶️ How to Run

### 1️⃣ Compile and Run Backend
```bash
javac NetworkScanner.java
java NetworkScanner

### Use Live Server to open:

http://127.0.0.1:5500/index.html
