#!/usr/bin/env bash

MYDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
REPO_HOME=${MYDIR}/..

ISTIO_VERSION=0.6.0
ISTIO_HOME=${HOME}/istio-${ISTIO_VERSION}
ISTIO_PROJECT=istio-system

export PATH="$PATH:${ISTIO_HOME}/bin"

function fail() {
    echo "$1"
    exit 1
}

function install_istio() {
    # install istio
    cd $HOME
    curl -kL https://git.io/getLatestIstio | sed 's/curl/curl -k /g' | ISTIO_VERSION=${ISTIO_VERSION} sh -
    export PATH="$PATH:${ISTIO_HOME}/bin"
    cd ${ISTIO_HOME}

    oc new-project istio-system
    oc adm policy add-scc-to-user anyuid -z istio-ingress-service-account
    oc adm policy add-scc-to-user privileged -z istio-ingress-service-account
    oc adm policy add-scc-to-user anyuid -z istio-egress-service-account
    oc adm policy add-scc-to-user privileged -z istio-egress-service-account
    oc adm policy add-scc-to-user anyuid -z istio-pilot-service-account
    oc adm policy add-scc-to-user privileged -z istio-pilot-service-account
    oc adm policy add-scc-to-user anyuid -z istio-grafana-service-account -n istio-system
    oc adm policy add-scc-to-user anyuid -z istio-prometheus-service-account -n istio-system
    oc adm policy add-scc-to-user anyuid -z prometheus -n istio-system
    oc adm policy add-scc-to-user privileged -z prometheus
    oc adm policy add-scc-to-user anyuid -z grafana -n istio-system
    oc adm policy add-scc-to-user privileged -z grafana
    oc adm policy add-scc-to-user anyuid -z default
    oc adm policy add-scc-to-user privileged -z default
    oc adm policy add-cluster-role-to-user cluster-admin -z default

    oc apply -f install/kubernetes/istio.yaml
    oc create -f install/kubernetes/addons/prometheus.yaml
    oc create -f install/kubernetes/addons/grafana.yaml
    oc create -f install/kubernetes/addons/servicegraph.yaml
    oc apply -f https://raw.githubusercontent.com/jaegertracing/jaeger-kubernetes/master/all-in-one/jaeger-all-in-one-template.yml

    oc expose svc grafana
    oc expose svc servicegraph
    oc expose svc jaeger-query
    oc expose svc istio-ingress
    oc expose svc prometheus
}

oc get clusterrolebindings >& /dev/null || fail "you do not appear to be logged in as a cluster-admin"

# install istio if it hasn't been installed get
oc get route istio-ingress -n istio-system  >& /dev/null || install_istio

# workaround for https://github.com/istio/issues/issues/34
setenforce 0 >& /dev/null || echo "no setenforce found; ignoring"


# install prod env
oc new-project prod || echo "Project prod already exists, skipping creation"
oc project prod
oc adm policy add-scc-to-user privileged -z default
oc adm policy add-scc-to-user anyuid -z default
oc adm policy add-role-to-user view system:serviceaccount:$(oc project -q):default -n $(oc project -q)

# deploy catalog
oc process -f $REPO_HOME/openshift/catalog-template.yml \
  GIT_URI=http://gogs.lab-infra:3000/developer/catalog.git \
  | istioctl kube-inject -f - | oc apply -f -

# deploy inventory infra (does not include deployment so no istio needed)
oc process -f $REPO_HOME/openshift/inventory-svc-template.yml | oc apply -f -

# deploy inventory v1
oc process -f $REPO_HOME/openshift/inventory-deployment-template.yml \
  SERVICE_VERSION=v1 \
  | istioctl kube-inject -f - | oc apply -f -

# deploy inventory v2 with built-in delay
oc process -f $REPO_HOME/openshift/inventory-deployment-template.yml \
  SERVICE_VERSION=v2 \
  SERVICE_DELAY=5000 \
  | istioctl kube-inject -f - | oc apply -f -

# deploy web
# TODO

# add ingress
# add istio ingress
cat <<EOF | oc create -f -
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: lab-ingress
  annotations:
    kubernetes.io/ingress.class: "istio"
spec:
  backend:
    serviceName: web-ui
    servicePort: http
  rules:
  - http:
      paths:
      - path: /services/inventory/.*
        backend:
          serviceName: inventory
          servicePort: http
      - path: /services/products
        backend:
          serviceName: catalog
          servicePort: http
EOF

function istio_endpoint() {
  echo "http://$(oc get route $1 -n ${ISTIO_PROJECT} --template='{{ .spec.host }}')"
}

echo
echo
echo -----------------------------------------
echo "Done! You should now wait a bit for everything to come up (run: oc get pods -w )"
echo -----------------------------------------
echo "TODO: Primary web frontend URL: $(istio_endpoint istio-ingress)"
echo "Example Inventory URL: $(istio_endpoint istio-ingress)/services/inventory/329299"
echo "Example Catalog URL: $(istio_endpoint istio-ingress)/services/products"
echo
echo "D3 force layout service graph: $(istio_endpoint servicegraph)/force/forcegraph.html?time_horizon=5m&filter_empty=true"
echo "Example Prometheus query: $(istio_endpoint prometheus)/graph?g0.range_input=30m&g0.expr=istio_request_count&g0.tab=0"
echo "Grafana Istio Dashboard: $(istio_endpoint grafana)/d/1/istio-dashboard?refresh=5s&orgId=1"
echo "Jaeger Tracing Console: $(istio_endpoint jaeger-query)"
echo
echo
