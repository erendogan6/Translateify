# Seyahat Asistanım

 <img src="https://i.hizliresim.com/g0u2wxk.png" width="230" height="230">

## Proje Hakkında
**Translateify**, İngilizce öğrenmek isteyen Türk kullanıcılar için tasarlanmış bir Android uygulamasıdır. Uygulama, eğlenceli ve etkileşimli bir öğrenme deneyimi sunar ve kullanıcıların kelime hazinelerini geliştirirken aynı zamanda dil becerilerini de pratik etmelerine yardımcı olur. Günlük kelime önerileri, çeviri egzersizleri, ve konuşma tanıma gibi özellikler sayesinde, kullanıcılar dil öğrenme yolculuklarını kişiselleştirebilir ve hedeflerine ulaşabilirler.

Uygulama şu anda Google Play Store'da test aşamasında.

 <img src="https://i.hizliresim.com/akcilji.jpeg" width="280" height="630">

## Özellikler

- **Günün Kelimesi:** Her gün yeni bir kelime önerisi ve bu kelimenin anlamı ile kullanım örnekleri.
- **Kelime Hazinesi Geliştirme:** Hatırlamak istediğiniz kelimeleri kaydedin, öğrendiğiniz veya öğrenmediğiniz olarak işaretleyin.
- **Anında Çeviri:** Karşılaştığınız herhangi bir kelimeyi anında çevirin.
- **Etkileşimli Öğrenme:** Görseller, çeviriler ve örnek cümlelerle kelime dağarcığınızı geliştirin.
- **Konuşma Tanıma:** Telaffuz ve dinleme becerilerinizi konuşma tanıma ile geliştirin.
- **Özelleştirilebilir Öğrenme Yolları:** Öğrenme seviyenizi seçin ve kendi öğrenme yolunuzu kişiselleştirin.

  
## Teknoloji Yığını (Tech Stack)
- **Kotlin (2.0)**
- **Android Jetpack:** Navigation, Flow, ViewModel, Room (architecture components)
- **Retrofit & OkHttp** (networking and HTTP requests)
- **GSON** (JSON serialization/deserialization)
- **Glide** (image loading)
- **Generative AI (Gemini)** (AI-powered text generation)
- **Flexmark** (Markdown rendering)
- **Lottie** (animated vector graphics)
- **Swipe to Refresh** (UI gesture for refreshing data)
- **Chucker** (HTTP traffic inspection)
- **Text to Speech** (TTS functionality)
- **Speech to Text** (voice recognition)
- **Firebase:** Auth, Firestore, Remote Config, Analytics, Crashlytics
- **LeakCanary** (memory leak detection)
- **Coroutines** (asynchronous programming)
- **ProGuard** (code obfuscation and optimization)
- **Hilt** (dependency injection)
- **Ktlint** (code style checking)
- **Material Design** (modern UI components and guidelines)


## Test Yığını (Test Stack)
- **JUnit** (Unit Testing)
- **MockK** (Mocking Framework)
- **Kotlin Coroutines Test** (Testing Coroutines)
- **Kover** (Code Coverage)
- **AssertJ** (Fluent Assertions)

## Yaklaşımlar (Approaches)
- **%100 Localization** (Türkçe, İngilizce)
- **Content Descriptions for Accessibility** (Tüm resimler için Türkçe ve İngilizce içerik açıklamaları)
- **SOLID Principles**
- **Clean Code**
- **DRY (Don't Repeat Yourself)**
- **KISS (Keep It Simple, Stupid)**

## Mimari (Architect)
- **MVVM** (Model-View-ViewModel)

## Katmanlar (Layers)
- **Data Layer**: Veri yönetimi (Room Database, Retrofit)
- **Domain Layer**: İş mantığı (Use Cases, Repository Interface)
- **Presentation Layer**: Kullanıcı arayüzü (Compose, ViewModel)

## Test Edilen Sürümler
- Android 9.0
- Android 10.0
- Android 11.0
- Android 12.0
- Android 13.0
- Android 14.0

## Kurulum

- Uygulamayı kullanmak için öncelikle Android Studio'yu bilgisayarınıza kurmanız gerekmektedir. Daha sonra aşağıdaki adımları takip edebilirsiniz:
- Bu repoyu yerel makinenize klonlayın:
```bash
git clone https://github.com/erendogan6/translateify.git
```
- Android Studio'yu açın ve "Open an existing project" seçeneğini kullanarak indirdiğiniz projeyi seçin.
- Projeyi açtıktan sonra gereken bağımlılıkların indirilmesini bekleyin.
- Gerekli API'leri local.properties içerisine girin.
- Uygulamayı bir Android cihazda veya emülatörde çalıştırın.

## Katkıda Bulunma ##

Projeye katkıda bulunmak isteyenler için katkı kuralları ve adımları CONTRIBUTING.md dosyasında açıklanmıştır.

##  Lisans ## 
Bu proje MIT Lisansı altında lisanslanmıştır.
