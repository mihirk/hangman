VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.box = "hashicorp/precise32"
  config.vm.network "private_network", ip: "192.168.33.10"
  config.vm.provision "shell", path: "provision/install_ansible.sh"

  config.vm.provision "ansible" do |ansible|
    ansible.playbook = "provision/vagrant.yml"
    ansible.inventory_path = "provision/vagrant_ansible_inventory"
  end
end
