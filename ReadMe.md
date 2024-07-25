## Get Started

1. Clone the repo

```
git clone https://github.com/Sp00n-1st/auth-base-v2.git
```

2. Install Maven Packages

```
mvn install
```

3. Run the application

```
mvn spring-boot:run
```

## Penjelasan

Project ini hasil implementasi atau pemecahan masalah dari studi case AUTH 2 dan AUTH 3
pada project ini sudah implementasi JwtFilter dimana jika token sudah expired maka sistem
akan menolak request, selain itu juga pada response login terdapat 2 jenis token yaitu access
token dan refresh token, terdapat parameter remember me juga pada API login untuk sebagai
penanda jika user centang remember me agar mendapatkan access token yang expired nya lebih lama
, selain itu tersedia juga API untuk logout agar user yang sudah logout tidak bisa menggunakan
access token nya lagi.
Selain itu project ini juga kurang lebih sudah handle solusi untuk AUTH 3 dimana diperlukan nya fitur
auto logout, disini saya menggunakan cara dengan menyimpan access token yang aktif, dengan begitu
untuk setiap request itu akan dilakukan pengecekan apakah token nya masih terdaftar atau tidak
meskipun jika access token belum expired tapi token nya sudah tidak terdaftar maka tetap tidak akan bisa
akses resource, saya juga menambakan sebuah action pada saat login yaitu jika user sudah login satu device
dan login lagi di device yang berbeda maka token yang lama akan direplace sehingga token yang lama tidak
akan bisa digunakan.
