#!/bin/bash

psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname = 'pci_ms'" | grep -q 1 || psql -U postgres -c "CREATE DATABASE pci_ms"
