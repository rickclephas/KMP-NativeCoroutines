// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "KMPNativeCoroutines",
    platforms: [.iOS(.v9), .macOS(.v10_10), .tvOS(.3), .watchOS(.v9)],
    products: [
        .library(
            name: "KMPNativeCoroutinesCore",
            targets: ["KMPNativeCoroutinesCore"]
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