# KMP-NativeCoroutines

A library to use Kotlin Coroutines from Swift code in KMP apps.

## Kotlin

### Flows

Create an extension property to expose the `Flow` as a `NativeFlow` to Swift:

```kotlin
val Clock.timeNative
    get() = time.asNativeFlow()
```

### Suspend functions

Create an extension function to expose the suspend function as a `NativeSuspend` to Swift:

```kotlin
fun RandomLettersGenerator.getRandomLettersNative() =
    nativeSuspend { getRandomLetters() }
```

## Swift Combine

### Flows

Use the `createPublisher(for:)` function to get an `AnyPublisher` for the `NativeFlow`:

```swift
let publisher = createPublisher(for: clock.timeNative)
```

### Suspend functions

Use the `createFuture(for:)` function to get an `AnyPublisher` for the `NativeSuspend`:

```swift
let future = createFuture(for: randomLettersGenerator.getRandomLettersNative())
```