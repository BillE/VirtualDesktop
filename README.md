# DataClearNavigation
Java Application to launch DataClear Black Box

In order to allow a regular user to add a wifi connection we added file:
myOverrides

to directory:

/etc/sudoers.d/

with content:

dataclear	ALL=(ALL)	NOPASSWD:/usr/bin/nmcli
dataclear	ALL=(ALL)	NOPASSWD:/usr/local/sbin/openvpn

-----------------

In /usr/lib directory, put file:
libchilkat.so from Chilkat for Java6 Linux 64 bit

-----------------

Put pem files in 
{user.home}/.ssh/
cPubKey.pem
cPrivKey.pem


