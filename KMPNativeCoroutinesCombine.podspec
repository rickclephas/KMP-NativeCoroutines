Pod::Spec.new do |s|
  s.name       = 'KMPNativeCoroutinesCombine'
  s.version    = '1.0.0-ALPHA-18'
  s.summary    = 'Swift library for Kotlin Coroutines with Combine'

  s.homepage   = 'https://github.com/rickclephas/KMP-NativeCoroutines'
  s.license    = 'MIT'
  s.authors    = 'Rick Clephas'

  s.source = {
    :git => 'https://github.com/rickclephas/KMP-NativeCoroutines.git',
    :tag => 'v' + s.version.to_s
  }

  s.swift_versions = ['5.0']
  s.ios.deployment_target = '13.0'
  s.osx.deployment_target = '10.15'
  s.watchos.deployment_target = '6.0'
  s.tvos.deployment_target = '13.0'

  s.dependency 'KMPNativeCoroutinesCore', s.version.to_s
  s.framework = 'Combine'

  s.source_files = 'KMPNativeCoroutinesCombine/**/*.swift'
end
