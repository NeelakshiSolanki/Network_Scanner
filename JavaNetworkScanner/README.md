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

---
<img width="1088" height="823" alt="Screenshot 2025-08-04 154020" src="https://github.com/user-attachments/assets/d6af0067-79dc-46c7-8936-bec23fd3f593" />

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
