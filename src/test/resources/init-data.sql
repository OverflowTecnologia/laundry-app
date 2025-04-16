
DELETE FROM public.machine;
DELETE FROM public.condominium;
INSERT INTO public.condominium (id, address, contact_phone, email, name) VALUES (1, '123 Main St', '123456789', 'test@test.com', 'Central Park');
INSERT INTO public.machine (id, condominium_id, identifier, type) VALUES (101, 1, 'test-identifier', 'Washer');