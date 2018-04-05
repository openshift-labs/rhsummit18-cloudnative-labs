# Lab Solutions

Each lab folder contains the solution files for the lab as well as an Ansible playbook 
called `solve.yml` which solves the lab automatically. 

## Run Solution Playbooks

* [Install Ansible](http://docs.ansible.com/ansible/latest/installation_guide/intro_installation.html)
* Customize the variables for your cluster. You can find the list of all variables in the included [vars.yml](vars.yml)

  ```
  cat <<'EOF' > /tmp/vars.yml

  openshift_master: https://dev.myopenshift.com:8443

  gogs_user: developer
  gogs_hostname: gogs-lab-infra.apps.myopenshift.com
  EOF
  ```

* Run the solution playbook

  ```
  ansible-playbook solutions/lab-1/solve.yml -e @/tmp/vars.yml
  ansible-playbook solutions/lab-2/solve.yml -e @/tmp/vars.yml
  ```