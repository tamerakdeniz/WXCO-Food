# WXCO Food - Food Delivery App

WXCO Food is a modern Android food delivery application built with Kotlin using MVVM architecture, Room database, Retrofit for API calls, and Hilt for dependency injection.

## Features

### 🏠 Home Screen

- Display all available foods in a grid layout
- Search functionality to find specific foods
- Smart cart integration:
  - Add new items to cart with one tap
  - Shows warning if item already exists in cart
- Add/remove foods from favorites
- Pull-to-refresh to update food list
- Modern Material Design 3 UI

### ❤️ Favorites

- View all favorited foods
- Remove foods from favorites
- Smart cart integration:
  - Add new items to cart with one tap
  - Shows warning if item already exists in cart
- Empty state with animations

### 🛒 Shopping Cart

- View all items in cart with quantities
- Smart quantity management:
  - Increase/decrease quantities within cart
  - Remove items when quantity reaches zero
- See total price calculation
- Checkout functionality
- Empty cart state

### 🍕 Food Details

- Large food image with parallax scrolling
- Smart cart integration:
  - Custom quantity selector with +/- buttons
  - Replaces existing cart item if already in cart
  - Updates quantity directly
- Toggle favorite status
- Total price calculation

## Technical Architecture

### 🏗️ Architecture Pattern

- **MVVM (Model-View-ViewModel)** with LiveData
- **Repository Pattern** for data management
- **Clean Architecture** principles

### 🛠️ Technologies Used

- **Kotlin** - Primary programming language
- **Android Jetpack** - Navigation, Room, ViewModel, LiveData
- **Hilt** - Dependency injection
- **Retrofit** - REST API client
- **Room** - Local database for favorites and cart
- **Glide** - Image loading and caching
- **Material Design 3** - Modern UI components
- **ViewBinding** - Type-safe view references
- **Coroutines** - Asynchronous programming

### 📦 Project Structure

```
app/
├── src/main/java/com/wxco/food/
│   ├── data/
│   │   ├── local/          # Room database, DAOs
│   │   ├── model/          # Data models
│   │   ├── remote/         # API service
│   │   └── repository/     # Repository implementations
│   ├── di/                 # Dependency injection modules
│   ├── ui/
│   │   ├── adapter/        # RecyclerView adapters
│   │   ├── fragment/       # UI fragments
│   │   └── viewmodel/      # ViewModels
│   ├── utils/              # Utility classes
│   ├── MainActivity.kt     # Main activity
│   └── WXCOFoodApplication.kt
└── res/
    ├── layout/             # XML layouts
    ├── drawable/           # Icons and drawables
    ├── navigation/         # Navigation graph
    ├── menu/               # Bottom navigation menu
    └── values/             # Strings, colors, styles
```

## Cart Management Logic

### Different Behaviors by Context

1. **Home & Favorites Screen**

   - Checks if item exists in cart
   - Shows warning if already in cart
   - Adds new items with quantity 1

2. **Detail Screen**

   - Allows custom quantity selection
   - Replaces existing cart item if present
   - Updates with new selected quantity

3. **Cart Screen**
   - Direct quantity manipulation
   - Increment/decrement controls
   - Auto-removes items at zero quantity

## API Integration

The app integrates with the following API endpoints:

- **GET** `/yemekler/tumYemekleriGetir.php` - Fetch all foods
- **POST** `/yemekler/sepeteYemekEkle.php` - Add food to cart
- **POST** `/yemekler/sepettekiYemekleriGetir.php` - Get cart items
- **POST** `/yemekler/sepettenYemekSil.php` - Remove from cart

All API requests are made on behalf of user `tamer_akdeniz`.

## Local Database

### Tables

- **favorite_foods** - Stores user's favorite foods
- **cart_foods** - Local cart storage (for offline support)

## Setup Instructions

1. **Clone the repository**

   ```bash
   git clone [repository-url]
   cd wxcofood
   ```

2. **Open in Android Studio**

   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the project folder

3. **Build the project**

   ```bash
   ./gradlew assembleDebug
   ```

4. **Run on device/emulator**
   - Connect Android device or start emulator
   - Click "Run" in Android Studio

## Requirements

- **Android SDK 24+** (Android 7.0)
- **Compile SDK 35** (Android 15)
- **Java 11**
- **Internet connection** for API calls

## Key Features Implementation

### 🔄 State Management

- Uses `Resource<T>` sealed class for handling Loading, Success, and Error states
- LiveData for reactive UI updates
- Repository pattern centralizes data operations

### 🌐 Network Layer

- Retrofit with Gson converter for JSON parsing
- OkHttp logging interceptor for debugging
- Error handling with user-friendly messages
- Timeout configurations for better UX

### 💾 Local Storage

- Room database for favorites and offline cart
- Automatic data persistence
- Type converters for complex data types

### 🎨 UI/UX

- Material Design 3 components
- Dark/Light theme support
- Smooth animations and transitions
- Pull-to-refresh functionality
- Empty states with illustrations
- Loading indicators

### 🧭 Navigation

- Single Activity with multiple Fragments
- Navigation Component with SafeArgs
- Bottom Navigation for main screens
- Proper back stack management

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is created for educational purposes.

---

**WXCO Food** - Delivering great food, one tap at a time! 🚀
