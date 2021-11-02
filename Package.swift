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
            name: "KMPNativeCoroutinesAsync",
            targets: ["KMPNativeCoroutinesAsync"]
        )
    ],
    targets: [
        .target(
            name: "KMPNativeCoroutinesCore",
            path: "KMPNativeCoroutinesCore"
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
        )
    ],
    swiftLanguageVersions: [.v5]
)