Pod::Spec.new do |s|
  s.name       = 'KMPNativeCoroutinesRxSwift'
  s.version    = '0.4.2'
  s.summary    = 'Swift library for Kotlin Coroutines with RxSwift'

  s.homepage   = 'https://github.com/rickclephas/KMP-NativeCoroutines'
  s.license    = 'MIT'
  s.authors    = 'Rick Clephas'

  s.source = {
    :git => 'https://github.com/rickclephas/KMP-NativeCoroutines.git',
    :tag => 'v' + s.version.to_s
  }

  s.swift_versions = ['5.0']
  s.ios.deployment_target = '9.0'
  s.osx.deployment_target = '10.9'

  s.dependency 'KMPNativeCoroutinesCore'
  s.dependency 'RxSwift'

  s.source_files = 'KMPNativeCoroutinesRxSwift/**/*.swift'
end
