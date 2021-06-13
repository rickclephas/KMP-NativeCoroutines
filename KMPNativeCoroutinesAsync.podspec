Pod::Spec.new do |s|
  s.name       = 'KMPNativeCoroutinesAsync'
  s.version    = '0.1.0'
  s.summary    = 'Swift library for Kotlin Coroutines with Swift Async/Await'

  s.homepage   = 'https://github.com/rickclephas/KMP-NativeCoroutines'
  s.license    = 'MIT'
  s.authors    = 'Rick Clephas'

  s.source = {
    :git => 'https://github.com/rickclephas/KMP-NativeCoroutines.git',
    :tag => 'v' + s.version.to_s
  }

  s.ios.deployment_target = '15.0'
  s.osx.deployment_target = '12.0'

  s.swift_version = '5.5'

  s.dependency 'KMPNativeCoroutinesCore'

  s.source_files = 'KMPNativeCoroutinesAsync/**/*.swift'
end