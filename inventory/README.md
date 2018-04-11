# Build Inventory Image

Build on OpenShift
```
oc new-build centos/nodejs-8-centos7 --binary --name=inventory
oc start-build inventory --from-dir=.
```

Build on Docker with S2I
```
# download S2I https://github.com/openshift/source-to-image/releases
s2i build . centos/nodejs-8-centos7 inventory
```