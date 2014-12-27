#!/bin/sh

install_ansible() {
    sudo apt-get install -y software-properties-common
    sudo apt-add-repository -y ppa:ansible/ansible
    sudo apt-get -y update
    sudo apt-get install -y ansible
}

ansible() {
    if [ -x "$(command -v ansible)" ]; then
        echo "Ansible is already installed"
    else
        install_ansible
    fi
}
ansible