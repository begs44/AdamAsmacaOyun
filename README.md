# 🎮 Adam Asmaca (Hangman)

Java Swing kullanılarak geliştirilmiş, şifre korumalı, skor ve log kayıt sistemine sahip etkileşimli klasik Adam Asmaca oyunu. SDÜ Bilgisayar Mühendisliği standartlarında, temiz ve anlaşılır bir mimari ile geliştirilmiştir.

## ✨ Özellikler

* **Güvenli Giriş Sistemi:** Uygulamaya ilk girişte şifre belirleme ve sonraki girişlerde 3 haklı şifre doğrulama ekranı.
* **Dinamik Kelime Havuzu:** Oyun içi kelimelerin harici bir `.txt` dosyasından okunması sayesinde kolayca genişletilebilir kelime dağarcığı.
* **Zaman Takibi:** Her oyun oturumu için saniye bazında entegre zamanlayıcı.
* **Kayıt ve Loglama:** 
  * **Oyun Skorları:** Kazanma/kaybetme durumu, süre ve tarih bilgileriyle listelenir.
  * **Sistem Logları:** Başarılı/başarısız giriş denemeleri ve şifre oluşturma işlemleri anlık olarak kaydedilir.
* **Yetkili İşlemleri:** Skor ve log tablolarını temizlemek için şifreli onay mekanizması.

## 📸 Ekran Görüntüleri

> *Buraya projenin ekran görüntülerini (örn: `![Oyun Ekranı](link)` şeklinde) ekleyebilirsin.*
> 
> * **Oyun Ekranı:** Oyundan bir an.
> * **Skorlar & Loglar:** Tabloların bulunduğu sekmeler.

## 🚀 Kurulum ve Çalıştırma

Projede dosya yolları lokal dizine göre ayarlandığından, uygulamayı çalıştırmadan önce aşağıdaki dizin yapısının oluşturulması gerekmektedir.

### 1. Dizin Yapısının Kurulması
Bilgisayarınızın `C:\` dizini altında şu klasörleri oluşturun:
* `C:\P2Oyun\Resimler\`
* `C:\P2Oyun\TXTDosyalar\`

### 2. Gerekli Dosyaların Eklenmesi
* **Resimler:** `Resimler` klasörünün içine `1.jpg`'den başlayarak hata sayısına göre ilerleyen adam asmaca görsellerini ekleyin. *(Örn: 1.jpg, 2.jpg ... 12.jpg)*
* **Kelimeler:** `TXTDosyalar` klasörü içinde bir `kelimeler.txt` dosyası oluşturun ve her satıra bir kelime gelecek şekilde tahmin edilecek kelimeleri yazın.

### 3. Projeyi Derleme
Gereksinimler: **Java JDK 8 veya üzeri**.

Projeyi tercih ettiğiniz IDE (NetBeans, IntelliJ, Eclipse) üzerinden açıp `OyunProje.java` dosyasını çalıştırabilirsiniz.

## 🛠️ Kullanılan Teknolojiler

* **Dil:** Java
* **Arayüz:** Java Swing (AWT, JFrame, JPanel)
* **Dosya Yönetimi:** `java.io` (FileReader, FileWriter, vb.)
* **Zaman Yönetimi:** `javax.swing.Timer`
