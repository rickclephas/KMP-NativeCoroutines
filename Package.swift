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
            name: "KMPNativeCoroutinesCombine",
            targets: ["KMPNativeCoroutinesCombine"]
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
        )
    ],
    swiftLanguageVersions: [.v5]
)
