// swift-tools-version:5.7
import PackageDescription

let package = Package(
    name: "KMPNativeCoroutines",
    platforms: [.iOS(.v15), .macOS(.v12), .tvOS(.v15), .watchOS(.v9)],
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
