// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "KMPNativeCoroutines",
    platforms: [.iOS(.v13), .macOS(.v10_15), .tvOS(.v13), .watchOS(.v6)],
    products: [
        .library(
            name: "KMPNativeCoroutinesCore",
            targets: ["KMPNativeCoroutinesCore"]
        ),
        .library(
            name: "KMPNativeCoroutinesCombine",
            targets: ["KMPNativeCoroutinesCombine"]
        ),
        .library(
            name: "KMPNativeCoroutinesAsync",
            targets: ["KMPNativeCoroutinesAsync"]
        ),
        .library(
            name: "KMPNativeCoroutinesRxSwift",
            targets: ["KMPNativeCoroutinesRxSwift"]
        )
    ],
    dependencies: [
        .package(
            url: "https://github.com/ReactiveX/RxSwift.git",
            from: "6.0.0"
        )
    ],
    targets: [
        .target(
            name: "KMPNativeCoroutinesCore",
            path: "KMPNativeCoroutinesCore"
        ),
        .target(
            name: "KMPNativeCoroutinesCombine",
            dependencies: ["KMPNativeCoroutinesCore"],
            path: "KMPNativeCoroutinesCombine"
        ),
        .testTarget(
            name: "KMPNativeCoroutinesCombineTests",
            dependencies: ["KMPNativeCoroutinesCombine"],
            path: "KMPNativeCoroutinesCombineTests"
        ),
        .target(
            name: "KMPNativeCoroutinesAsync",
            dependencies: ["KMPNativeCoroutinesCore"],
            path: "KMPNativeCoroutinesAsync"
        ),
        .testTarget(
            name: "KMPNativeCoroutinesAsyncTests",
            dependencies: ["KMPNativeCoroutinesAsync"],
            path: "KMPNativeCoroutinesAsyncTests"
        ),
        .target(
            name: "KMPNativeCoroutinesRxSwift",
            dependencies: ["KMPNativeCoroutinesCore", "RxSwift"],
            path: "KMPNativeCoroutinesRxSwift"
        ),
        .testTarget(
            name: "KMPNativeCoroutinesRxSwiftTests",
            dependencies: ["KMPNativeCoroutinesRxSwift"],
            path: "KMPNativeCoroutinesRxSwiftTests"
        )
    ],
    swiftLanguageVersions: [.v5]
)