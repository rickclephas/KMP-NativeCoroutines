Pod::Spec.new do |s|
  s.name       = 'KMPNativeCoroutinesCore'
  s.version    = '1.0.0-ALPHA-43'
  s.summary    = 'Swift library for Kotlin Coroutines'

  s.homepage   = 'https://github.com/rickclephas/KMP-NativeCoroutines'
  s.license    = 'MIT'
  s.authors    = 'Rick Clephas'

  s.source = {
    :git => 'https://github.com/rickclephas/KMP-NativeCoroutines.git',
    :tag => 'v' + s.version.to_s
  }

  s.swift_versions = ['5.0']
  s.ios.deployment_target = '9.0'
  s.osx.deployment_target = '10.13'
  s.watchos.deployment_target = '3.0'
  s.tvos.deployment_target = '9.0'

  s.source_files = 'KMPNativeCoroutinesCore/**/*.swift'
end
